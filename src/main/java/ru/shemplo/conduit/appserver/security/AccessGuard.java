package ru.shemplo.conduit.appserver.security;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.GuardRuleEntity;
import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.repositories.GuardRuleEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.snowball.utils.MiscUtils;

@RequiredArgsConstructor
@Component ("accessGuard")
public final class AccessGuard {
    
    private final GuardRuleEntityRepository rulesRepository;
    
    private final ConcurrentMap <String, Set <OptionEntity>> requirements 
          = new ConcurrentHashMap <> ();
    
    public void method (Method method) { 
        method (method, PeriodEntity.getSystem ()); 
    }
    
    public void method (Method method, PeriodEntity period) {
        final Set <OptionEntity> options = getRequirements (method.getName ());
        
        Authentication authentication = SecurityContextHolder.getContext ()
                                      . getAuthentication ();
        if (!(authentication.getPrincipal () instanceof WUser)) {
            throw new SecurityException ("Not WUser");
        }
        
        WUser user = MiscUtils.cast (authentication.getPrincipal ());
        // Everything is allowed for administrator accounts
        if (user.getEntity ().isAdmin ()) { return; }
        
        if (options.isEmpty () && !user.getEntity ().isAdmin ()) {
            throw new SecurityException ("Not protected method");
        }
        
        // Intersecting sets with existing and needed options
        long intersection = user.getOptions (period).stream ()
                          . filter (options::contains)
                          . count  ();
        if (intersection != options.size ()) {
            throw new SecurityException ("Not enough rights");
        }
    }
    
    public void invalidateRequirements (Method method) {
        requirements.remove (method.getName ());
    }
    
    private Set <OptionEntity> getRequirements (String object) {
        return requirements.computeIfAbsent (object, name -> {
            final GuardRuleEntity rule = rulesRepository.findByObject (object);
            if (rule == null) { return new HashSet <> (); }
            
            return Optional.ofNullable (rule.getRequirements ())
                 . orElse  (new HashSet <> ()).stream ()
                 . collect (Collectors.toSet ());
        });
    }
    
}
