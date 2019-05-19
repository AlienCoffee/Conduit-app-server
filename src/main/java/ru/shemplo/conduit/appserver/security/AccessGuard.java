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
    
    public void page (Method controllerMethod, String handle) {
        //method (controllerMethod, PeriodEntity.getSystem (), null);
    }
    
    public void page (Method controllerMethod, String handle, PeriodEntity period) {
        //method (controllerMethod, period, null);
    }
    
    public void method (Method method) { 
        method (method, PeriodEntity.getSystem (), null); 
    }
    
    public void method (Method method, PeriodEntity period, WUser target) {
        long start = System.currentTimeMillis ();
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
            System.out.println ("Unprotected: " + method.getName ());
            throw new SecurityException ("Not protected method");
        }
        
        // Intersecting sets with existing and needed options
        long intersection = user.getOptions (period).stream ()
                          . filter (options::contains)
                          . count  ();
        if (intersection != options.size ()) {
            // User don't have enough rights for method but 
            // he asked for data that also belongs to him
            if (target != null && target.equals (user)) {
                return;
            }
            
            System.out.println ("Not enough rights: " + method.getName ());
            System.out.println ("Required: " + options);
            System.out.println ("User: " + user.getOptions (period));
            
            throw new SecurityException ("Not enough rights");
        }
        
        long end = System.currentTimeMillis ();
        System.out.println (String.format ("Check access for %s to %s [time ~%dms]", 
            user.getEntity ().getLogin (), method.getName (), end - start));
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
