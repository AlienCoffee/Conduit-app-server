package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.*;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataCollector;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.services.*;
import ru.shemplo.conduit.appserver.start.MethodsScanner;
import ru.shemplo.conduit.appserver.web.ResponseBox;
import ru.shemplo.conduit.appserver.web.dto.BlogPostDTO;
import ru.shemplo.conduit.appserver.web.dto.GroupMember;
import ru.shemplo.conduit.appserver.web.form.WebFormRow;
import ru.shemplo.snowball.stuctures.Pair;

@RestController
@RequiredArgsConstructor
public class GetController {
    
    private final PersonalDataService personalDataService;
    private final BlogPostsService blogPostsService;
    private final OlympiadsService olympiadsService;
    private final MethodsScanner methodsScanner;
    private final MethodsService methodsService;
    private final PeriodsService periodsService;
    private final OptionsService optionsService;
    private final GroupsService groupsService;
    private final RolesService rolesService;
    private final UsersService usersService;
    private final Clock clock;
    
    
    @GetMapping (API_GET_PERIODS) 
    public ResponseBox <Collection <PeriodEntity>> handleGetPeriods () {
        return ResponseBox.ok (periodsService.getAllPeriods ());
    }
    
    @GetMapping (API_GET_USERS) 
    public ResponseBox <Collection <UserEntity>> handleGetUsers () {
        return ResponseBox.ok (usersService.getAllUsers ());
    }
    
    @GetMapping (API_GET_OPTIONS) 
    public ResponseBox <Collection <OptionEntity>> handleGetOptions () {
        return ResponseBox.ok (optionsService.getAllOptions ());
    }
    
    @GetMapping (API_GET_METHODS) 
    public ResponseBox <Collection <String>> handleGetMethods () {
        List <String> methods = methodsScanner.getProtectedMethods ().values ().stream ()
                              . map     (Method::getName).sorted  ()
                              . collect (Collectors.toList ());
        return ResponseBox.ok (methods);
    }
    
    @GetMapping (API_GET_GUARD_RULES) 
    public ResponseBox <Collection <GuardRuleEntity>> handleGetGuardRules () {
        return ResponseBox.ok (methodsService.getGuardMethodsRules ());
    }
    
    @GetMapping (API_GET_ROLES) 
    public ResponseBox <Collection <RoleEntity>> handleGetRoles () {
        return ResponseBox.ok (rolesService.getAllRoles ());
    }
    
    @GetMapping (API_GET_PERIOD_REGISTER_ROLES) 
    public ResponseBox <Map <String, List <WebFormRow>>> handleGetPeriodRegisterRoles () {
        return ResponseBox.ok (rolesService.getPeriodRegisterTemplates ());
    }
    
    @PostMapping (API_GET_PERIOD_REGISTERED) 
    public ResponseBox <Map <String, List <UserEntity>>> handleGetPeriodRegistered (
        @RequestParam ("period") Long periodID
    ) {
        final PeriodEntity period = periodsService.getPeriod (periodID);
        
        return ResponseBox.ok (rolesService.getPeriodRegisteredUsers (period));
    }
    
    @PostMapping (API_GET_PERSONAL_DATA) 
    public ResponseBox <PersonalDataCollector> handleGetPersonalData (
        @RequestParam ("period") Long periodID,
        @RequestParam ("user")   Long userID
    ) {
        final PeriodEntity period = periodsService.getPeriod (periodID);
        final WUser user = usersService.getUser (userID);
        
        return ResponseBox.ok (personalDataService.getPersonalData (user, period));
    }
    
    @PostMapping (API_GET_GROUP_TYPES) 
    public ResponseBox <List <Pair <String, String>>> handleGetGroupTypes () {
        return ResponseBox.ok (groupsService.getGroupTypes ());
    }
    
    @PostMapping (API_GET_PERIOD_GROUPS) 
    public ResponseBox <List <GroupEntity>> handleGetGroups (
        @RequestParam ("period") Long periodID
    ) {
        final PeriodEntity period = periodsService.getPeriod (periodID);
        return ResponseBox.ok (groupsService.getPeriodGroups (period));
    }
    
    @PostMapping (API_GET_GROUP_MEMBERS) 
    public ResponseBox <List <GroupMember>> handleGetGroupMembers (
        @RequestParam ("group") Long groupID
    ) {
        final GroupEntity group = groupsService.getGroup (groupID);
        return ResponseBox.ok (groupsService.getGroupMembers (group));
    }
    
    @PostMapping (API_GET_OLYMPIADS) 
    public ResponseBox <List <OlympiadEntity>> handleGetOlympiads (
        @RequestParam ("group") Long groupID
    ) {
        final GroupEntity group = groupsService.getGroup (groupID);
        return ResponseBox.ok (olympiadsService.getOlympiadsByGroup (group));
    }
    
    @PostMapping (API_GET_MAIN_CHANNEL_BLOG_POSTS) 
    public ResponseBox <List <BlogPostDTO>> handleGetMainChannelBlogPosts (
        @RequestParam (value = "group", required = false) String since
    ) {
        LocalDateTime border = (since != null && since.length () > 0)
                             ? LocalDateTime.parse (since) 
                             : LocalDateTime.now (clock);
        List <BlogPostEntity> posts = blogPostsService
           . getMainChannelPosts (border);
        
        List <BlogPostDTO> dtos = posts.stream ().map (post -> {
                final String title = post.getTitle (), content = post.getContent ();
                final String author = post.getCommitter ().getLogin ();
                final LocalDateTime issued = post.getPublished ();
                
                BlogPostDTO dto = new BlogPostDTO (title, content, author, issued);
                return dto;
            })
            . collect (Collectors.toList ());
        
        
        return ResponseBox.ok (dtos).addParam ("more", false);
    }
    
}
