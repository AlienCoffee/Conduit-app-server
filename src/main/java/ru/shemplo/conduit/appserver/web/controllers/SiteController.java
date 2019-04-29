package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;
import static javax.servlet.http.HttpServletResponse.*;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteController {
    
    @GetMapping ($)
    public ModelAndView handleIndexPage (Principal principal,
            HttpServletResponse response) {
        ModelAndView view = new ModelAndView ("index");
        view.addObject ("user", principal);
        return view;
    }
    
    @GetMapping ("/account")
    public ModelAndView handleAccountPage (Principal principal,
            HttpServletResponse response) {
        ModelAndView view = new ModelAndView ("account");
        view.addObject ("user", principal);
        return view;
    }
    
    @GetMapping (PAGE_LOGIN)
    public ModelAndView handleLoginPage (Principal principal,
            HttpServletResponse response) {
        if (principal != null) { // Already authorized
            response.setStatus (SC_MOVED_TEMPORARILY);
            response.setHeader ("Location", "/");
            
            return null;
        }
        
        return new ModelAndView ("login");
    }
    
}
