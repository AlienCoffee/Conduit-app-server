package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.ValidationException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.PeriodStatus;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupType;
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.services.*;
import ru.shemplo.conduit.appserver.utils.FormValidator;
import ru.shemplo.conduit.appserver.web.ResponseBox;

@RestController
@RequiredArgsConstructor
public class CreateController {
    
    //private final GroupAssignmentsService groupAssignmentsService;
    private final PersonalDataService personalDataService;
    private final VerificationService verificationService;
    private final PeriodsService periodsService;
    private final OptionsService optionsService;
    private final GroupsService groupsService;
    //private final FilesService filesService;
    private final PostsService postsService;
    private final RolesService rolesService;
    private final UsersService usersService;
    private final Clock clock;
    
    private final Lock REG_LOCK = new ReentrantLock (true);
    
    @PostMapping (API_CREATE_USER)
    public ResponseBox <Void> handleCreateUser (
        @RequestParam ("login")    String login,
        @RequestParam ("phone")    String phone,
        @RequestParam ("password") String password,
        @RequestParam (value = "secret", required = false)   
            String secret
    ) {
        try   { 
            login    = FormValidator.validateLogin (login); 
            phone    = FormValidator.validatePhone (phone);
            password = FormValidator.validatePassword (password);
        } catch (ValidationException ve) {
            return ResponseBox.fail (ve.getMessage ());
        }
        
        final String vphone = FormValidator.formatPhone (phone);
        
        //System.out.println ("Before danger zone " + Thread.currentThread ().getName ());
        REG_LOCK.lock ();
        //System.out.println ("Enter to danger zone " + Thread.currentThread ().getName ());
        
        try {            
            if (usersService.loadUserByUsername (login) != null 
                    || verificationService.isLoginPending_ss (login)) {
                String message = "This login is already used";
                
                REG_LOCK.unlock ();
                return ResponseBox.fail (message);
            }
        } catch (UsernameNotFoundException unfe) {
            // it's expected and needed
        }
        
        try {            
            if (usersService.loadUserByUsername (phone) != null 
                    || verificationService.isPhonePending_ss (phone)) {
                String message = "This phone number is already used";
                
                REG_LOCK.unlock ();
                return ResponseBox.fail (message);
            }
        } catch (UsernameNotFoundException unfe) {
            // it's expected and needed
        }
        
        if (secret != null && secret.length () > 0) {
            if (verificationService.checkCodeAndDelete_ss (login, vphone, password, secret)) {
                usersService.createUser (login, vphone, password);
            } else {
                String message = "Wrong verification code";
                
                REG_LOCK.unlock ();
                return ResponseBox.fail (message);
            }
        } else {            
            verificationService.createCode_ss (login, vphone, password);
        }
        
        REG_LOCK.unlock ();
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
        @RequestParam (value = "template", required = false) 
            String templateName,
        @RequestParam ("name") String name
    ) {
        PersonalDataTemplate template = !(templateName == null || "".equals (templateName.trim ()))
                                      ? PersonalDataTemplate.valueOf (templateName)
                                      : null;
        rolesService.createRole (name.trim (), template);
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
        final LocalDateTime untilDT = until != null && until.length () > 0 
                                    ? LocalDateTime.parse (until) : null;
        final LocalDateTime sinceDT = LocalDateTime.parse (since);
        final PeriodStatus status = PeriodStatus.CREATED;
        periodsService.createPeriod (name, description, 
                       sinceDT, untilDT, status, user);
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_PERIOD_REGISTRATION)
    public ResponseBox <Void> handleCreatePeriodRegistration (
        @IndentifiedUser           WUser  user,
        @RequestParam ("template") String template,
        @RequestParam ("period")   Long periodID,
        @RequestParam ("data")     Map <String, String> data
    ) {
        final PersonalDataTemplate temp = PersonalDataTemplate.forName (template);
        final PeriodEntity period = periodsService.getPeriod (periodID);
        
        try {
            // TODO: The first argument (editable user) must be provided as argument from client
            personalDataService.savePersonalData (user, period, temp, data, user);
        } catch (IllegalStateException ise) {
            return ResponseBox.fail (ise);
        }
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_GROUP)
    public ResponseBox <Void> handleCreateGroup (
        @IndentifiedUser         WUser  user,
        @RequestParam ("name")   String name,
        @RequestParam ("period") Long periodID,
        @RequestParam ("type")   String typeName,
        @RequestParam (value = "description", required = false)  
            String description
    ) {
        final PeriodEntity period = periodsService.getPeriod (periodID);
        final GroupType type = GroupType.valueOf (typeName);
        
        groupsService.createGroup (name, description, period, type, user);
        return ResponseBox.ok ();
    }
    
    /*
    @PostMapping (API_CREATE_GROUP_ASSIGNMENT)
    public ResponseBox <Void> handleCreateGroupAssignment (
        @IndentifiedUser           WUser user,
        @RequestParam ("user")     Long userID,
        @RequestParam ("group")    Long groupID,
        @RequestParam ("status")   String statusName,
        @RequestParam ("comment")  String comment,
        @RequestParam (value = "role", required = false)     
            Long roleID
    ) {
        final GroupAssignmentStatus status = GroupAssignmentStatus.valueOf (statusName);
        final RoleEntity role = roleID == null ? null : rolesService.getRole (roleID);
        final GroupEntity group = groupsService.getGroup (groupID);
        final WUser target = usersService.getUser (userID);
        
        groupsService.createGroupAssignment (target, role, group, status, comment, user);
        return ResponseBox.ok ();
    }
    */
    
    @PostMapping (API_CREATE_GROUP_JOIN)
    public ResponseBox <Void> handleCreateGroupJoin (
        @IndentifiedUser        WUser user,
        @RequestParam ("group") Long groupID
    ) {
        final GroupEntity group = groupsService.getGroup (groupID);
        
        groupsService.createGroupJoin (user, group);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_INFORMATION_POST)
    public ResponseBox <Void> handleCreateInformationPost (
        @IndentifiedUser          WUser user,
        @RequestParam ("group")   Long groupID,
        @RequestParam ("title")   String title,
        @RequestParam ("content") String content
    ) {
        final LocalDateTime publishTime = LocalDateTime.now (clock);
        final GroupEntity group = groupsService.getGroup (groupID);
        postsService.createInforamtionPost (group, title, content, 
                                            publishTime, user);
        
        return ResponseBox.ok ();
    }
    
}
