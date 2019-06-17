package ru.shemplo.conduit.appserver.web.controllers;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.AssignmentStatus;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.entities.data.RegisteredPeriodRoleEntity;
import ru.shemplo.conduit.appserver.entities.groups.*;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadProblemEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.services.*;
import ru.shemplo.conduit.appserver.web.dto.AttachmentFileRow;
import ru.shemplo.conduit.appserver.web.dto.CheckingAttemptRow;
import ru.shemplo.conduit.appserver.web.dto.GroupMember;
import ru.shemplo.conduit.appserver.web.dto.PageGroupRow;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.MiscUtils;

@Controller
@RequiredArgsConstructor
public class SiteController {
    
    private final GroupAssignmentsService groupAssignmentsService;
    private final OlympiadAttemptsService olympiadAttemptsService;
    private final OlympiadProblemsService olympiadProblemsService;
    private final OlympaidChecksService olympaidChecksService;
    private final PersonalDataService personalDataService;
    private final OlympiadsService olympiadsService;
    private final PeriodsService periodsService;
    private final GroupsService groupsService;
    private final FilesService filesService;
    private final PostsService postsService;
    private final RolesService rolesService;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @GetMapping ($)
    public ModelAndView handleIndexPage (
        @IndentifiedUser WUser user,
        HttpServletResponse response
    ) {
        UserEntity ent = user != null ? user.getEntity () : null;
        ModelAndView mav = new ModelAndView ("index");
        mav.addObject ("user", ent);
        return mav;
    }
    
    @ProtectedMethod
    @GetMapping ("/admin")
    public ModelAndView handleAccountPage (
        @IndentifiedUser WUser user,
        HttpServletResponse response
    ) {
        try {
            accessGuard.method (MiscUtils.getMethod ());
            
            UserEntity ent = user != null ? user.getEntity () : null;
            ModelAndView view = new ModelAndView ("admin");
            view.addObject ("user", ent);
            
            final List <GroupAssignmentEntity> gasses = groupAssignmentsService
                . getAllApplicationsWithStatus (AssignmentStatus.APPLICATION);
            
            gasses.sort (Comparator.comparing (GroupAssignmentEntity::getIssued));
            view.addObject ("group_join_applications", gasses);
            
            return view;
        } catch (SecurityException se) {
            response.setStatus (SC_FORBIDDEN);
            return null;
        }
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
        accessGuard.page (MiscUtils.getMethod (), "allPeriods");
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
        @IndentifiedUser     WUser user,
        @PathVariable ("id") Long periodID
    ) {
        ModelAndView mav = new ModelAndView ("period/period");
        
        final PeriodEntity period  = periodsService.getPeriod (periodID);
        mav.addObject ("period", period);
        
        mav.addObject ("group_types", GroupType.values ());
        
        try {            
            final List <RegisteredPeriodRoleEntity> roles 
                = rolesService.getUserRolesForPeriod (user, period);
            mav.addObject ("role_applications", roles);
            
            long amount = roles.stream ()
            . map    (RegisteredPeriodRoleEntity::getStatus)
            . filter (AssignmentStatus.ASSIGNED::equals)
            . count  ();
            mav.addObject ("have_assigned_roles", amount > 0);
        } catch (SecurityException se) {}
        
        try {            
            final Set <GroupEntity> userGroups = new HashSet <> (
                groupAssignmentsService.getUserGroups (user)
            );
            final Set <GroupEntity> userApplications = new HashSet <> (
                groupAssignmentsService.getUserApplications (user)
            );
            
            final Map <GroupType, List <PageGroupRow>> groups 
                = groupsService.getPeriodGroups (period).stream ().map (PageGroupRow::new)
                . peek    (row -> {
                    AssignmentStatus status = userGroups.contains (row.getGroup ())
                                            ? AssignmentStatus.ASSIGNED
                                            : userApplications.contains (row.getGroup ())
                                            ? AssignmentStatus.APPLICATION
                                            : AssignmentStatus.REJECTED;
                    GroupJoinType joinType = row.getGroup ().getJoinType ();
                    row.setJoinType (joinType);
                    row.setStatus (status);
                })
                . collect (Collectors.groupingBy (row -> row.getGroup ().getType ()));
            for (GroupType type : GroupType.values ()) {
                List <PageGroupRow> list = groups.get (type);
                if (list != null) {
                    list.sort (Comparator.comparing (row -> row.getGroup ().getName ()));
                }
            }
            
            mav.addObject ("have_access_to_groups", true);
            mav.addObject ("groups", groups);
        } catch (SecurityException se) {
            mav.addObject ("have_access_to_groups", false);
        }
        
        return mav;
    }
    
    @GetMapping (PAGE_PERIOD_REGISTRATION)
    public ModelAndView handlePeriodRegistrationPage (
        @IndentifiedUser     WUser user,
        @PathVariable ("id") Long periodID
    ) {
        final PeriodEntity period = periodsService.getPeriod (periodID);
        ModelAndView mav = new ModelAndView ("period/registration");
        mav.addObject ("period", period);
        mav.addObject ("user", user);
        
        try {            
            final List <RegisteredPeriodRoleEntity> roles 
                = rolesService.getUserRolesForPeriod (user, period);
            mav.addObject ("role_applications", roles);
            
            long amount = roles.stream ()
            . map    (RegisteredPeriodRoleEntity::getStatus)
            . filter (AssignmentStatus.ASSIGNED::equals)
            . count  ();
            mav.addObject ("have_assigned_roles", amount > 0);
        } catch (SecurityException se) {}
        
        final List <PersonalDataTemplate> templates = personalDataService
            . getUserRegisteredTemplates (user, period);
        mav.addObject ("templates", templates);
        
        return mav;
    }
    
    @GetMapping (PAGE_GROUP)
    public ModelAndView handleGroupPage (
        @PathVariable ("id") Long groupID
    ) {
        ModelAndView mav = new ModelAndView ("period/group");
        
        final GroupEntity group = groupsService.getGroup (groupID);
        
        mav.addObject ("period", group.getPeriod ());
        mav.addObject ("group", group);
        
        List <PostEntity> posts = postsService.getPostsByGroup (group);
        posts.sort (Comparator.comparing (PostEntity::getPublished));
        mav.addObject ("posts", posts);
        
        List <OlympiadEntity> olympiads = olympiadsService.getOlympiadsByGroup (group);
        olympiads.sort (Comparator.comparing (OlympiadEntity::getPublished));
        mav.addObject ("olympiads", olympiads);
        
        List <GroupMember> members = groupsService.getGroupMembers (group);
        members.sort (Comparator.<GroupMember, String> comparing (m -> m.getRole ().getTemplate ().name ())
                                .thenComparing (m -> m.getUser ().getLogin ()));
        mav.addObject ("members", members);
        
        return mav;
    }
    
    @GetMapping (PAGE_OLYMPIAD)
    public ModelAndView handleOlympiadPage (
        @IndentifiedUser     WUser user,
        @PathVariable ("id") Long olympiadID
    ) {
        ModelAndView mav = new ModelAndView ("period/olympiad");
        
        final OlympiadEntity olympiad = olympiadsService.getOlympiad (olympiadID);
        final GroupEntity group = olympiad.getGroup ();
        
        mav.addObject ("period", group.getPeriod ());
        mav.addObject ("olympiad", olympiad);
        mav.addObject ("group", group);
        mav.addObject ("user", user);
        
        LocalDateTime now = LocalDateTime.now (clock);
        mav.addObject ("is_olympiad_finished", !now.isBefore (olympiad.getFinished ()));
        
        List <OlympiadProblemEntity> problems = olympiadProblemsService
           . getProblemsByOlympiad (olympiad);
        problems.sort (Comparator.comparing (OlympiadProblemEntity::getId));
        mav.addObject ("problems", problems);
        
        final int attemptsNumber = olympiadAttemptsService
        . getRemainingUserAttemptsNumber (user, olympiad);
        mav.addObject ("remaining_attempts", attemptsNumber);
        
        List <OlympiadAttemptEntity> attempts = olympiadAttemptsService
           . getUserAttempts (user, olympiad);
        attempts.sort (Comparator.comparing (OlympiadAttemptEntity::getId).reversed ());
        mav.addObject ("attempts", attempts);
        
        return mav;
    }
    
    @GetMapping (PAGE_OLYMPIAD_ATTEMPTS)
    public ModelAndView handleOlympiadAttemptsPage (
        @IndentifiedUser     WUser user,
        @PathVariable ("id") Long olympiadID
    ) {
        ModelAndView mav = new ModelAndView ("period/olympiad_attempts");
        
        final OlympiadEntity olympiad = olympiadsService.getOlympiad (olympiadID);
        final GroupEntity group = olympiad.getGroup ();
        
        mav.addObject ("period", group.getPeriod ());
        mav.addObject ("olympiad", olympiad);
        mav.addObject ("group", group);
        
        List <OlympiadAttemptEntity> attempts = olympiadAttemptsService
           . getAttemptsForCheck (olympiad);
        List <CheckingAttemptRow> attemptsRows = attempts.stream ()
           . map (attempt -> {
               final Pair <Integer, Integer> results = olympaidChecksService
                   . getNumberOfCheckedProblemsAndScoreByUser (attempt, user);
               boolean checked = olympaidChecksService.isAttemptChecked (attempt);
               return new CheckingAttemptRow (attempt, results.F, results.S, checked);
           })
           . collect (Collectors.toList ());
           
        mav.addObject ("attempts", attemptsRows);
        
        return mav;
    }
    
    @GetMapping (PAGE_ATTEMPT_CHECK)
    public ModelAndView handleAttemptCheckPage (
        @IndentifiedUser     WUser user,
        @PathVariable ("id") Long attemptID
    ) {
        ModelAndView mav = new ModelAndView ("period/olympiad_check_attempt");
        OlympiadAttemptEntity attempt = olympiadAttemptsService.getAttempt (attemptID);
        
        final OlympiadEntity olympiad = attempt.getOlympiad ();
        final GroupEntity group = olympiad.getGroup ();
        
        mav.addObject ("period", group.getPeriod ());
        mav.addObject ("olympiad", olympiad);
        mav.addObject ("attempt", attempt);
        mav.addObject ("group", group);
        
        final List <AttachmentFileRow> filesRows = filesService
        . getEntriesInAttemptArchive (attempt, user).stream ()
        . map     (entry -> {
            String path = entry.getName ();
            long size = entry.getSize ();
            
            String name = path; int index = -1;
            if ((index = path.lastIndexOf ('/')) != -1) {
                name = path.substring (index + 1);
            }
            
            return new AttachmentFileRow (name, path, size + "b");
        })
        . collect (Collectors.toList ());
        mav.addObject ("files", filesRows);
        
        List <OlympiadProblemEntity> problems = olympiadProblemsService
           . getProblemsByOlympiad (olympiad);
        mav.addObject ("problems", problems);
        
        return mav;
    }
    
}
