package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.IndentifiedUser;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.services.*;
import ru.shemplo.conduit.appserver.start.MethodsScanner;
import ru.shemplo.conduit.appserver.web.ResponseBox;

@RestController
@RequiredArgsConstructor
public class UpdateController {
    
    private final MethodsScanner methodsScanner;
    private final MethodsService methodsService;
    private final OptionsService optionsService;
    private final PeriodsService periodsService;
    private final RolesService rolesService;
    private final WUserService usersService;
    
    @PostMapping (API_UPDATE_ADD_METHOD_RULE)
    public ResponseBox <Void> handleAddMethodRule (
        @RequestParam ("method")   String methodName,
        @RequestParam ("optionID") Long   optionID
    ) {
        Method method = methodsScanner.getMethodByName (methodName);
        OptionEntity option = optionsService.getOption (optionID);
        methodsService.addRequirementToMethod (method, option);
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_REMOVE_METHOD_RULE)
    public ResponseBox <Void> handleRemoveMethodRule (
        @RequestParam ("method") String methodName,
        @RequestParam ("optionID") Long optionID
    ) {
        final Method method = methodsScanner.getMethodByName (methodName);
        final OptionEntity option = optionsService.getOption (optionID);
        methodsService.removeRequirementFromMethod (method, option);
        
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_ADD_ROLE_OPTION)
    public ResponseBox <Void> handleAddRoleOption (
        @RequestParam ("roleID")   Long roleID,
        @RequestParam ("optionID") Long optionID
    ) {
        OptionEntity option = optionsService.getOption (optionID);
        RoleEntity role = rolesService.getRole (roleID);
        rolesService.addOptionToRole (role, option);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_REMOVE_ROLE_OPTION)
    public ResponseBox <Void> handleRemoveRoleOption (
        @RequestParam ("roleID")   Long roleID,
        @RequestParam ("optionID") Long optionID
    ) {
        final OptionEntity option = optionsService.getOption (optionID);
        final RoleEntity role = rolesService.getRole (roleID);
        rolesService.removeOptionFromRole (role, option);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_ADD_ROLE_TO_USER)
    public ResponseBox <Void> handleAddRoleToUser (
        @IndentifiedUser           WUser committer,
        @RequestParam ("userID")   Long userID,
        @RequestParam ("periodID") Long periodID,
        @RequestParam ("roleID")   Long roleID
    ) {
        PeriodEntity period = periodsService.getPeriod (periodID);
        RoleEntity role = rolesService.getRole (roleID);
        WUser user = usersService.getUser (userID);
        
        usersService.addRole (user, period, role, committer);
        return ResponseBox.ok ();
    }
    
    @PostMapping (API_UPDATE_REMOVE_ROLE_FROM_USER)
    public ResponseBox <Void> handleRemoveRoleFromUser (
        @RequestParam ("userID") Long userID,
        @RequestParam ("periodID") Long periodID,
        @RequestParam ("roleID") Long roleID
    ) {
        PeriodEntity period = periodsService.getPeriod (periodID);
        RoleEntity role = rolesService.getRole (roleID);
        WUser user = usersService.getUser (userID);
        
        usersService.removeRole (user, period, role);
        return ResponseBox.ok ();
    }
    
}
