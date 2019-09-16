package ru.shemplo.conduit.appserver.security;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class SecurityEntryPoint extends LoginUrlAuthenticationEntryPoint {
    
    private static boolean isXMLOrAPIHttpRequest (HttpServletRequest request) {
        return "XMLHttpRequest".equals (request.getHeader ("X-Requested-with"))
            || request.getServletPath ().startsWith (API_);
    }

    public SecurityEntryPoint (String loginFormUrl) { super (loginFormUrl); }
    
    @Override
    public void commence (HttpServletRequest request, HttpServletResponse response,
            AuthenticationException ae) throws IOException, ServletException {
        if (isXMLOrAPIHttpRequest (request)) { // ajax requests should be just dropped
            response.sendError(SC_UNAUTHORIZED, "Forbidden (not authorized)");
        } else { super.commence (request, response, ae); }
    }
    
}
