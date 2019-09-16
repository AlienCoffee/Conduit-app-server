package ru.shemplo.conduit.appserver.security;

import static org.springframework.security.web.csrf.CookieCsrfTokenRepository.*;
import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.services.UsersService;
import ru.shemplo.conduit.appserver.web.ResponseBox;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity (prePostEnabled = false, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    private final SuccessfulLogoutHandler successfulLogoutHandler;
    private final SuccessfulLoginHandler successfulLoginHandler;
    private final FailedLoginHandler failedLoginHandler;
    @Autowired private UsersService userService;
    
    @Bean
    public static BCryptPasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder ();
    }
    
    @Bean
    public CookieCsrfTokenRepository tokenRepository () {
        return withHttpOnlyFalse ();
    }
    
    @Component
    @RequiredArgsConstructor
    public static class FailedLoginHandler implements AuthenticationFailureHandler {

        private final ObjectMapper omapper;
        
        @Override
        public void onAuthenticationFailure (HttpServletRequest request, 
                HttpServletResponse response, AuthenticationException ae) 
                throws IOException, ServletException {
            final StringWriter result = new StringWriter ();
            response.setStatus (HttpServletResponse.SC_OK);
            
            omapper.writeValue (result, ResponseBox.fail (ae.getMessage ()));
            response.getWriter ().write (result.toString ());
            response.getWriter ().flush ();
        }
        
    }
    
    @Component
    public static class SuccessfulLoginHandler implements AuthenticationSuccessHandler {
        
        @Override
        public void onAuthenticationSuccess (HttpServletRequest request, 
                HttpServletResponse response, Authentication authentication) 
                throws IOException, ServletException {
            response.setStatus (HttpServletResponse.SC_OK);
            response.getWriter ().write ("{\"authorized\": true}");
        }
        
    }
    
    @Component
    public static class SuccessfulLogoutHandler implements LogoutSuccessHandler {

        @Override
        public void onLogoutSuccess (HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter ().write ("{\"authorized\": false}");
        }
        
    }
    
    @Autowired
    public void configureGlobal (final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService (userService).passwordEncoder (passwordEncoder ());
    }

    
    @Override
    public void configure (WebSecurity web) throws Exception {
        web.ignoring ().antMatchers ("/resources/**", "/favicon.ico");
    }
    
    @Override
    protected void configure (HttpSecurity http) throws Exception {
        http.antMatcher ("/**")
            .authorizeRequests ()
                .antMatchers ($, PAGE_LOGIN, API_UNCHECKED_ + "**", "/error").permitAll ()
                .anyRequest ().authenticated ()
                .and ()
            .exceptionHandling ()
                .authenticationEntryPoint (new SecurityEntryPoint (PAGE_LOGIN))
                .and ()
            .formLogin ()
                .loginPage          (PAGE_LOGIN)
                .loginProcessingUrl (API_LOGIN)
                .successHandler     (successfulLoginHandler)
                .failureHandler     (failedLoginHandler)
                .permitAll ()
                .and ()
            .logout ()
                .logoutSuccessHandler (successfulLogoutHandler)
                .logoutUrl (API_LOGOUT)
                .logoutSuccessUrl ($)
                .and ()
            .rememberMe ()
                .key ("Q3o.28K&b53y41Jv5SVF6ePj&208'306mNnU")
                .userDetailsService (userService)
                .and ()
            .csrf ()
                .csrfTokenRepository (withHttpOnlyFalse ());
    }
    
}
