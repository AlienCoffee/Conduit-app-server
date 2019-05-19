package ru.shemplo.conduit.appserver.web.controllers;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupType;
import ru.shemplo.conduit.appserver.entities.groups.PostEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadProblemEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.services.*;
import ru.shemplo.conduit.appserver.web.dto.GroupMember;
import ru.shemplo.snowball.utils.MiscUtils;

@Controller
@RequiredArgsConstructor
public class SiteController {
    
    private final OlympiadProblemsService olympiadProblemsService;
    private final OlympiadsService olympiadsService;
    private final PeriodsService periodsService;
    private final GroupsService groupsService;
    private final PostsService postsService;
    private final AccessGuard accessGuard;
    
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
        
        final Map <GroupType, List <GroupEntity>> groups 
            = groupsService.getPeriodGroups (period).stream ()
            . collect (Collectors.groupingBy (GroupEntity::getType));
        
        List <GroupEntity> list = groups.get (GroupType.ELIMINATION);
        if (list != null) {
            list.sort (Comparator.comparing (GroupEntity::getName));
            mav.addObject ("ELIMINATION_groups", list);
        }
        
        list = groups.get (GroupType.STUDY);
        if (list != null) {
            list.sort (Comparator.comparing (GroupEntity::getName));
            mav.addObject ("STUDY_groups", list);            
        }
        
        list = groups.get (GroupType.INFO);
        if (list != null) {
            list.sort (Comparator.comparing (GroupEntity::getName));
            mav.addObject ("INFO_groups", list);            
        }
        
        list = groups.get (GroupType.POOL);
        if (list != null) {
            list.sort (Comparator.comparing (GroupEntity::getName));
            mav.addObject ("POOL_groups", list);            
        }
        
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
        @PathVariable ("id") Long olympiadID
    ) {
        ModelAndView mav = new ModelAndView ("period/olympiad");
        
        final OlympiadEntity olympiad = olympiadsService.getOlympiad (olympiadID);
        final GroupEntity group = olympiad.getGroup ();
        
        mav.addObject ("period", group.getPeriod ());
        mav.addObject ("olympiad", olympiad);
        mav.addObject ("group", group);
        
        List <OlympiadProblemEntity> problems = olympiadProblemsService
           . getProblemsByOlympiad (olympiad);
        problems.sort (Comparator.comparing (OlympiadProblemEntity::getId));
        mav.addObject ("problems", problems);
        
        return mav;
    }
    
}
