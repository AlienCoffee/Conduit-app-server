package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.LocalDateTime;

import javax.validation.ValidationException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.services.StudyPeriodsService;
import ru.shemplo.conduit.appserver.services.WUserService;
import ru.shemplo.conduit.appserver.utils.PasswordValidator;
import ru.shemplo.conduit.appserver.utils.PhoneValidator;
import ru.shemplo.conduit.appserver.web.ResponseBox;
import ru.shemplo.snowball.utils.MiscUtils;

@RestController
@RequiredArgsConstructor
public class CreateController {
    
    private final StudyPeriodsService periodsService;
    //private final GroupsService groupsService;
    private final WUserService usersService;
    
    @PostMapping (API_CREATE_USER)
    public ResponseBox <Void> handleCreateUser (
        @RequestParam ("phone")     String phone,
        @RequestParam ("password")  String password,
        @RequestParam (value = "login", required = false)  
            String login
    ) {
        try   { phone = PhoneValidator.validate (phone); } 
        catch (ValidationException ve) {
            return ResponseBox.fail (ve.getMessage ());
        }
        
        try   { password = PasswordValidator.validate (password); } 
        catch (ValidationException ve) {
            return ResponseBox.fail (ve.getMessage ());
        }
        
        usersService.createUser (login, phone, password);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_PERIOD) 
    public ResponseBox <Void> handleCreatePeriod (
        Authentication authentication,
        @RequestParam ("name")  String name,
        @RequestParam ("since") String since,
        @RequestParam (value = "description", required = false)  
            String description,
        @RequestParam (value = "until", required = false)
            String until
    ) {
        WUser user = MiscUtils.cast (authentication.getPrincipal ());
        createPeriod (user, name.trim (), description, since, until);
        return ResponseBox.ok ();
    }
    
    private void createPeriod (WUser user, String name, String description, 
            final String since, final String until) {
        final LocalDateTime sinceDT = LocalDateTime.parse (since, RU_DATETIME_FORMAT);
        final LocalDateTime untilDT = until != null
                                    ? LocalDateTime.parse (until, RU_DATETIME_FORMAT)
                                    : null;
        periodsService.createPeriod (name, description, sinceDT, untilDT, true, user);
    }
    
    /*
    @PostMapping (API_CREATE_GROUP) 
    public ResponseBox <Void> handleCreateGroup (
        @IndentifiedUser WUser user,
        @RequestParam ("name")  String name,
        @RequestParam (value = "description", required = false)  
            String description,
        @RequestParam ("periodID") Long periodID
    ) {
        try {
            StudyPeriodEntity period = periodsService.getPeriod (periodID);   
            groupsService.createGroup (name, description, period, user);
        } catch (EntityNotFoundException enfe) {
            return ResponseBox.fail (enfe);
        }
        
        return ResponseBox.ok ();
    }
    */
    
}
