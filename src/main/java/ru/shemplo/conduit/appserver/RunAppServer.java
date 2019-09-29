package ru.shemplo.conduit.appserver;

import java.io.IOException;
import java.time.Clock;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.shemplo.conduit.appserver.start.DBValidator;
import ru.shemplo.conduit.appserver.start.MethodsScanner;
import ru.shemplo.conduit.appserver.utils.StringToMapConverter;

@SpringBootApplication (exclude = {SecurityAutoConfiguration.class})
public class RunAppServer {
    
    public static void main (String ... args) throws BeansException, IOException {
        final Class <?> MAIN_CLASS = RunAppServer.class;
        final ConfigurableApplicationContext context 
            = SpringApplication.run (MAIN_CLASS, args);
        
        context.getBean (MethodsScanner.class).scanMethods (context);
        context.getBean (DBValidator.class).validate ();
    }
    
    @Bean public static Clock getSystemClock () {
        return Clock.systemDefaultZone ();
    }
    
    @Configuration
    public static class ConvertesConfiguration implements WebMvcConfigurer {
        
        @Override
        public void addFormatters (FormatterRegistry registry) {
            WebMvcConfigurer.super.addFormatters (registry);
            registry.addConverter (new StringToMapConverter ());
        }
        
    }
    
}
