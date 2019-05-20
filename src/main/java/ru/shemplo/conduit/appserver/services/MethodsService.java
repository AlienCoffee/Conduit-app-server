package ru.shemplo.conduit.appserver.services;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.GuardRuleEntity;
import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.conduit.appserver.entities.repositories.GuardRuleEntityRepository;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class MethodsService {
    
    private final GuardRuleEntityRepository guardRulesRepository;
    private final AccessGuard accessGuard;
    
    @ProtectedMethod
    public void addRequirementToMethod (Method method, boolean self, OptionEntity option) {
        accessGuard.method (MiscUtils.getMethod ());
        
        GuardRuleEntity rule = guardRulesRepository.findByObject (method.getName ());
        if (rule == null) {
            rule = new GuardRuleEntity (method.getName (), self, new HashSet <> ());
        }
        
        rule.getRequirements ().add (option);
        guardRulesRepository.save (rule);
        accessGuard.invalidateAll ();
    }
    
    @ProtectedMethod
    public void removeRequirementFromMethod (Method method, OptionEntity option) {
        accessGuard.method (MiscUtils.getMethod ());
        
        GuardRuleEntity rule = guardRulesRepository.findByObject (method.getName ());
        if (rule == null) { return; /* everything is already done */ }
        
        rule.getRequirements ().remove (option);
        guardRulesRepository.save (rule);
        accessGuard.invalidateAll ();
    }
    
    @ProtectedMethod
    public Collection <GuardRuleEntity> getGuardMethodsRules () {
        accessGuard.method (MiscUtils.getMethod ());
        return guardRulesRepository.findAll ();
    }
    
}
