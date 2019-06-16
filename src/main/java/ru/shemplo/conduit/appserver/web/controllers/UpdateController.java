package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.*;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.services.*;
import ru.shemplo.conduit.appserver.start.MethodsScanner;
import ru.shemplo.conduit.appserver.web.ResponseBox;
import ru.shemplo.conduit.appserver.web.dto.CheckedOlympiadProblems;

@RestController
@RequiredArgsConstructor
public class UpdateController {
    
    private final GroupAssignmentsService groupAssignmentsService;
    private final OlympiadAttemptsService olympiadAttemptsService;
    private final OlympaidChecksService olympaidChecksService;
    private final OlympiadsService olympiadsService;
    private final MethodsScanner methodsScanner;
    private final MethodsService methodsService;
    private final OptionsService optionsService;
    private final PeriodsService periodsService;
    private final RolesService rolesService;
    private final UsersService usersService;
    
    @PostMapping (API_INVALIDATE_CACHES)
    public ResponseBox <Void> handleInvalidateCaches () {
        groupAssignmentsService.invalidateCache ();
        olympiadAttemptsService.invalidateCache ();
        olympaidChecksService.invalidateCache ();
        olympiadsService.invalidateCache ();
        periodsService.invalidateCache ();
        rolesService.invalidateCache ();
        usersService.invalidateCache ();
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_ADD_METHOD_RULE)
    public ResponseBox <Void> handleAddMethodRule (
        @RequestParam ("method")   String methodName,
        @RequestParam ("option") Long   optionID
    ) {
        Method method = methodsScanner.getMethodByName (methodName);
        OptionEntity option = optionsService.getOption (optionID);
        methodsService.addRequirementToMethod (method, true, option); // TODO: self allowed flag
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_REMOVE_METHOD_RULE)
    public ResponseBox <Void> handleRemoveMethodRule (
        @RequestParam ("method") String methodName,
        @RequestParam ("option") Long optionID
    ) {
        final Method method = methodsScanner.getMethodByName (methodName);
        final OptionEntity option = optionsService.getOption (optionID);
        methodsService.removeRequirementFromMethod (method, option);
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_ADD_ROLE_OPTION)
    public ResponseBox <Void> handleAddRoleOption (
        @RequestParam ("role")   Long roleID,
        @RequestParam ("option") Long optionID
    ) {
        OptionEntity option = optionsService.getOption (optionID);
        RoleEntity role = rolesService.getRole (roleID);
        rolesService.addOptionToRole (role, option);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_REMOVE_ROLE_OPTION)
    public ResponseBox <Void> handleRemoveRoleOption (
        @RequestParam ("role")   Long roleID,
        @RequestParam ("option") Long optionID
    ) {
        final OptionEntity option = optionsService.getOption (optionID);
        final RoleEntity role = rolesService.getRole (roleID);
        rolesService.removeOptionFromRole (role, option);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_ADD_ROLE_TO_USER)
    public ResponseBox <Void> handleAddRoleToUser (
        @IndentifiedUser         WUser committer,
        @RequestParam ("user")   Long userID,
        @RequestParam ("period") Long periodID,
        @RequestParam ("role")   Long roleID
    ) {
        PeriodEntity period = periodsService.getPeriod (periodID);
        RoleEntity role = rolesService.getRole (roleID);
        WUser user = usersService.getUser (userID);
        
        rolesService.changeUserRoleInPeriod (period, user, 
                   role, EntityAction.ADD, "", committer);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_REMOVE_ROLE_FROM_USER)
    public ResponseBox <Void> handleRemoveRoleFromUser (
        @IndentifiedUser         WUser committer,
        @RequestParam ("user")   Long userID,
        @RequestParam ("period") Long periodID,
        @RequestParam ("role")   Long roleID
    ) {
        PeriodEntity period = periodsService.getPeriod (periodID);
        RoleEntity role = rolesService.getRole (roleID);
        WUser user = usersService.getUser (userID);
        
        rolesService.changeUserRoleInPeriod (period, user, 
                role, EntityAction.REMOVE, "", committer);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_PERIOD_STATE)
    public ResponseBox <Void> handleChangePeriodState (
        @IndentifiedUser         WUser committer,
        @RequestParam ("period") Long periodID,
        @RequestParam ("status") String statusName
    ) {
        final PeriodEntity period = periodsService.getPeriod (periodID);
        final PeriodStatus status = PeriodStatus.valueOf (statusName);
        periodsService.changePeriodStatus (period, status, committer);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_GROUP_JOIN_APPLICATION)
    public ResponseBox <Void> handleChangeGroupJoinApplicationStatus (
        @IndentifiedUser              WUser committer,
        @RequestParam ("application") Long applicationID,
        @RequestParam ("status")      String statusName
    ) {
        final AssignmentStatus status = AssignmentStatus.valueOf (statusName);
        groupAssignmentsService.changeApplicationStatus (applicationID, 
                                                    status, committer);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_OLYMPIAD_RESULTS)
    public ResponseBox <Void> handleToggleOlympiadResults (
        @IndentifiedUser           WUser   committer,
        @RequestParam ("olympiad") Long    olympiadID,
        @RequestParam ("finalize") Boolean finalize
    ) {
        OlympiadEntity olympiad = olympiadsService.getOlympiad (olympiadID);
        olympiadsService.setResultsStatus (olympiad, finalize, committer);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_ATTEMPT_RESULTS)
    public ResponseBox <Void> handleSaveAttemptResults (
        @IndentifiedUser          WUser committer,
        @RequestParam ("attempt") Long  attemptID,
        @RequestParam ("results") CheckedOlympiadProblems results
    ) {
        OlympiadAttemptEntity attempt = olympiadAttemptsService.getAttempt (attemptID);
        olympaidChecksService.saveAttemptResults (attempt, results, committer);
        return ResponseBox.ok ();
    }
    
}
