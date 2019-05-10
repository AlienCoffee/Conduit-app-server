package ru.shemplo.conduit.appserver.web.controllers;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.conduit.appserver.services.MethodsService;
import ru.shemplo.conduit.appserver.services.OptionsService;
import ru.shemplo.conduit.appserver.start.MethodsScanner;
import ru.shemplo.conduit.appserver.web.ResponseBox;

@RestController
@RequiredArgsConstructor
public class UpdateController {
    
    private final MethodsScanner methodsScanner;
    private final MethodsService methodsService;
    private final OptionsService optionsService;
    
    @PostMapping (API_UPDATE_ADD_METHOD_RULE)
    public ResponseBox <Void> handleAddMethodRule (
        @RequestParam ("method") String methodName,
        @RequestParam ("optionID") Long optionID
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
    
}
