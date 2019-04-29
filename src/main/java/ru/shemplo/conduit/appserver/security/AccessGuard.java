package ru.shemplo.conduit.appserver.security;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.GuardRuleEntity;
import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.repositories.GuardRuleEntityRepository;

@RequiredArgsConstructor
@Component ("accessGuard")
public class AccessGuard {
    
    private static final Collection <String> UNSPECIFIED_RULE 
          = Arrays.asList ("%FAKE_OPTION%");
    
    private final GuardRuleEntityRepository rulesRepository;
    
    private final ConcurrentMap <String, Set <String>> requirements 
          = new ConcurrentHashMap <> ();
    
    public boolean type (final Authentication authentication, Object object) {
        if (object == null) { return false; }
        final String objectName = object.getClass ().getName ();
        return checkRequirements (authentication, getRequirements (objectName));
    }
    
    public boolean method (final Authentication authentication, Object object, String methodName) {
        if (object == null || methodName == null || methodName.length () == 0) { return false; }
        final String objectName = object.getClass ().getName () + "#" + methodName;
        return checkRequirements (authentication, getRequirements (objectName));
    }
    
    private Set <String> getRequirements (String object) {
        return requirements.computeIfAbsent (object, name -> {
            final GuardRuleEntity rule = rulesRepository.findByObject (object);
            if (rule == null) { return new HashSet <> (UNSPECIFIED_RULE); }
            
            Set <String> result = Optional.ofNullable (rule.getRequirements ())
                                . orElse  (new HashSet <> ()).stream ()
                                . map     (OptionEntity::getName)
                                . collect (Collectors.toSet ());
            if (result.isEmpty ()) { result.addAll (UNSPECIFIED_RULE); }
            return result;
        });
    }
    
    private boolean checkRequirements (Authentication authentication, Set <String> need) {
        int counter = 0;
        for (GrantedAuthority authority : authentication.getAuthorities ()) {
            if (authority.getAuthority ().equals (RoleEntity.HEAD_ROLE)) {
                return true; // for HEAD everything is allowed
            }
            
            if (need.contains (authority.getAuthority ().substring (1))) {
                counter++;
            }
        }
        
        return counter == need.size ();
    }
    
}
