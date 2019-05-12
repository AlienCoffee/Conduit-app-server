package ru.shemplo.conduit.appserver.web.controllers;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.services.PeriodsService;

@Controller
@RequiredArgsConstructor
public class SiteController {
    
    private final PeriodsService periodsService;
    
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
    
    @GetMapping (PAGE_PERIODS)
    public ModelAndView handlePeriodsPage () {
        ModelAndView mav = new ModelAndView ("period/periods");
        
        Collection <PeriodEntity> periodsC = periodsService.getAllPeriods ();
        List <PeriodEntity> periods = periodsC.stream ()
                                    . filter  (p -> !p.getName ().startsWith ("$"))
                                    . collect (Collectors.toList ());
        mav.addObject ("periods", periods);
        return mav;
    }
    
    @GetMapping (PAGE_PERIOD)
    public ModelAndView handlePeriodPage (
        @PathVariable ("id") Long periodID
    ) {
        ModelAndView mav = new ModelAndView ("period/period");
        
        return mav;
    }
    
    @GetMapping (PAGE_PERIOD_REGISTRATION)
    public ModelAndView handlePeriodRegistrationPage (
        @PathVariable ("id") Long periodID
    ) {
        final PeriodEntity period = periodsService.getPeriod (periodID);
        ModelAndView mav = new ModelAndView ("period/registration");
        mav.addObject ("period", period);
        
        return mav;
    }
    
}
