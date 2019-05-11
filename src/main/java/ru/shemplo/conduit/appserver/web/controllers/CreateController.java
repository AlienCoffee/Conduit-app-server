package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.LocalDateTime;

import javax.validation.ValidationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodStatus;
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.services.OptionsService;
import ru.shemplo.conduit.appserver.services.PeriodsService;
import ru.shemplo.conduit.appserver.services.RolesService;
import ru.shemplo.conduit.appserver.services.WUserService;
import ru.shemplo.conduit.appserver.utils.PasswordValidator;
import ru.shemplo.conduit.appserver.utils.PhoneValidator;
import ru.shemplo.conduit.appserver.web.ResponseBox;

@RestController
@RequiredArgsConstructor
public class CreateController {
    
    private final PeriodsService periodsService;
    private final OptionsService optionsService;
    //private final GroupsService groupsService;
    private final RolesService rolesService;
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
        
        final String vphone = PhoneValidator.format (phone);
        usersService.createUser (login, vphone, password);
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_OPTION)
    public ResponseBox <Void> handleCreateOption (
        @RequestParam ("name") String name
    ) {
        optionsService.createOption (name.trim ());
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_ROLE)
    public ResponseBox <Void> handleCreateRole (
        @RequestParam ("name") String name
    ) {
        rolesService.createRole (name.trim ());
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_PERIOD) 
    public ResponseBox <Void> handleCreatePeriod (
        @IndentifiedUser         WUser  user,
        @RequestParam ("name")   String name,
        @RequestParam ("since")  String since,
        @RequestParam (value = "description", required = false)  
            String description,
        @RequestParam (value = "until", required = false)
            String until
    ) {
        final LocalDateTime untilDT = until != null ? LocalDateTime.parse (until) : null;
        final LocalDateTime sinceDT = LocalDateTime.parse (since);
        final PeriodStatus status = PeriodStatus.CREATED;
        periodsService.createPeriod (name, description, 
                 sinceDT, untilDT, status, true, user);
        
        return ResponseBox.ok ();
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
