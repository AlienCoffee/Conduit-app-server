package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

import javax.validation.ValidationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.FileEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.PeriodStatus;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupType;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.services.*;
import ru.shemplo.conduit.appserver.utils.FormValidator;
import ru.shemplo.conduit.appserver.web.ResponseBox;

@RestController
@RequiredArgsConstructor
public class CreateController {
    
    //private final GroupAssignmentsService groupAssignmentsService;
    private final OlympiadAttemptsService olympiadAttemptsService;
    private final OlympiadProblemsService olympiadProblemsService;
    private final PersonalDataService personalDataService;
    private final OlympiadsService olympiadsService;
    private final PeriodsService periodsService;
    private final OptionsService optionsService;
    private final GroupsService groupsService;
    private final FilesService filesService;
    private final PostsService postsService;
    private final RolesService rolesService;
    private final UsersService usersService;
    private final Clock clock;
    
    @PostMapping (API_CREATE_USER)
    public ResponseBox <Void> handleCreateUser (
        @RequestParam ("login")    String login,
        @RequestParam ("phone")    String phone,
        @RequestParam ("password") String password
    ) {
        try   { login = FormValidator.validateLogin (login); } 
        catch (ValidationException ve) {
            return ResponseBox.fail (ve.getMessage ());
        }
        
        try   { phone = FormValidator.validatePhone (phone); } 
        catch (ValidationException ve) {
            return ResponseBox.fail (ve.getMessage ());
        }
        
        try   { password = FormValidator.validatePassword (password); } 
        catch (ValidationException ve) {
            return ResponseBox.fail (ve.getMessage ());
        }
        
        final String vphone = FormValidator.formatPhone (phone);
        //usersService.createUser (login, vphone, password);
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_VERIFY_USER)
    public ResponseBox <Void> handleVerifyUser (
        @RequestParam ("login")    String login,
        @RequestParam ("phone")    String phone,
        @RequestParam ("password") String password,
        @RequestParam ("secret")   String secret
    ) {
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
        final LocalDateTime untilDT = until != null ? LocalDateTime.parse (until) : null;
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
        @RequestParam Map <String, String> data
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
    
    @PostMapping (API_CREATE_OLYMPIAD)
    public ResponseBox <Void> handleCreateOlympiad (
        @IndentifiedUser          WUser user,
        @RequestParam ("group")   Long groupID,
        @RequestParam ("name")    String name,
        @RequestParam ("publish") String pusblishDate,
        @RequestParam ("finish")  String finishDate,
        @RequestParam (value = "description", required = false) 
            String description,
        @RequestParam (value = "attempts", required = false) 
            Integer attempts
    ) {
        final LocalDateTime publish = LocalDateTime.parse (pusblishDate);
        final LocalDateTime finish = LocalDateTime.parse (finishDate);
        final GroupEntity group = groupsService.getGroup (groupID);
        
        olympiadsService.createOlympiad (group, name, description, publish, finish, attempts, user);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_OLYMPIAD_PROBLEM)
    public ResponseBox <Void> handleCreateOlympiadProblem (
        @IndentifiedUser           WUser user,
        @RequestParam ("olympiad") Long olympiadID,
        @RequestParam ("title")    String title,
        @RequestParam ("content")  String content,
        @RequestParam ("cost")     Integer cost,
        @RequestParam (value = "difficulty", required = false) 
            Integer description
    ) {
        final OlympiadEntity olympiad = olympiadsService.getOlympiad (olympiadID);
        olympiadProblemsService.createOlympiadProblem (olympiad, title, content, 
                                                       cost, description, user);
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_CREATE_OLYMPIAD_ATTEMPT)
    public ResponseBox <Void> handleCreateOlympiadAttempt (
        @IndentifiedUser           WUser user,
        @RequestParam ("olympiad") Long olympiadID,
        @RequestParam ("comment")  String comment,
        @RequestParam ("file")     MultipartFile file
    ) throws IOException {
        final OlympiadEntity olympiad = olympiadsService.getOlympiad (olympiadID);
        if (olympiadAttemptsService.getRemainingUserAttemptsNumber (user, olympiad) == 0) {
            String message = "The number of available attempts is exceeded";
            throw new IllegalStateException (message);
        }
        
        FileEntity archive = filesService.saveOlympiadAttemptArchive (file, olympiad, user);
        olympiadAttemptsService.createAttempt (user, olympiad, archive);
        
        return ResponseBox.ok ();
    }
    
}
