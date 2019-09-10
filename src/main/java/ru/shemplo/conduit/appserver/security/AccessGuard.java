package ru.shemplo.conduit.appserver.security;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.*;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard.AccessEntity;
import ru.shemplo.conduit.appserver.services.AbsCachedService;
import ru.shemplo.conduit.appserver.services.GroupAssignmentsService;
import ru.shemplo.conduit.appserver.services.PeriodsService;
import ru.shemplo.conduit.appserver.services.UsersService;
import ru.shemplo.conduit.appserver.utils.Utils;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.stuctures.Trio;
import ru.shemplo.snowball.utils.MiscUtils;

@RequiredArgsConstructor
@Component ("accessGuard")
public final class AccessGuard extends AbsCachedService <AccessEntity> {
    
    @Autowired private GroupAssignmentsService groupAssignmentsService;
    @Autowired @Getter private SecurityService securityService;
    private final GuardRulesService guardRulesService;
    @Autowired private PeriodsService periodsService;
    @Autowired private UsersService usersService;
    
    protected class AccessEntity extends Trio <PeriodEntity, UserEntity, Set <String>> implements Identifiable {

        public AccessEntity (PeriodEntity F, UserEntity S, Set <String> T) { super (F, S, T); }
        
        @Getter private final Long id = Utils.hash2 (F, S);
        
    }
    
    @Override
    protected AccessEntity loadEntity (Long id) {
        Pair <Long, Long> pair = Utils.dehash2 (id);
        
        final UserEntity user = usersService.getUser_ss (pair.S).getEntity ();
        final PeriodEntity period = periodsService.getPeriod_ss (pair.F);
        return new AccessEntity (period, user, new HashSet <> ());
    }

    @Override
    protected int getCacheSize () { return 128; }
    
    public void invalidateForUserInPeriod (UserEntity user, PeriodEntity period) {
        securityService.invalidateForUserInPeriod (user, period);
        CACHE.invalidate (Utils.hash2 (period, user));
    }
    
    public void invalidateAll () { 
        securityService.invalidateAll ();
        guardRulesService.invalidate (); 
        CACHE.invalidate (); 
    }
    
    private WUser fetchContextUser () {
        Authentication authentication = SecurityContextHolder.getContext ()
                                      . getAuthentication ();
        /*
        if (!(authentication.getPrincipal () instanceof WUser)) {
            throw new SecurityException ("Your personality is not recognized");
        }
        */
        
        return authentication.getPrincipal () instanceof WUser
             ? MiscUtils.cast (authentication.getPrincipal ())
             : WUser.getStubUser ();
    }
    
    public void object (String object, PeriodEntity period, WUser target) {
        final WUser user = fetchContextUser ();
        
        // Everything is allowed for administrator accounts
        if (user.getEntity ().getIsAdmin ()) { return; }
        
        AccessEntity access = getEntity (Utils.hash2 (period, user));
        //System.out.println ("Access: " + access);
        synchronized (access) {
            // Check if access is granted for this user and period
            if (access.getT ().contains (object)) { return; }
            
            Optional <GuardRuleEntity> rule = guardRulesService.getRule (object);
            if (!rule.isPresent ()) {
                System.out.println ("Not protected object: " + object);
                throw new SecurityException ("Not protected method");
            }
            
            final Set <OptionEntity> userRights = securityService
            . getUserOptionsForPeriod (period, user.getEntity ());
            
            //System.out.println (Utils.toString ("Rights ", userRights));
            Set <OptionEntity> required = rule.get ().getRequirements ();
            boolean rememberAccess = true;
            //System.out.println (Utils.toString ("Required ", required));
            for (OptionEntity entity : required) {
                if (userRights.contains (entity)) { continue; }
                
                // User don't have enough rights for method but he asked for data that also belongs to him
                if   (rule.get ().getSelfAllowed () && target != null && user.equals (target)) { 
                    // Don't remember b/c it's weakening of rules but not access grant
                    rememberAccess = false; break; 
                } else {
                    System.out.println ("Object: " + object); 
                    throw new SecurityException ("Not enough rights"); 
                }
            }
            
            if (rememberAccess) { access.getT ().add (object); }
        }
    }
    
    public void method (Method method, PeriodEntity period, WUser target) {
        //final long start = System.currentTimeMillis ();
        object (method.getName (), period, target);
        
        /*
        final long end = System.currentTimeMillis ();
        System.out.println (String.format ("Method: %s, time: %dms", 
                                   method.getName (), end - start));*/
    }
    
    public void method (Method method, PeriodEntity period) { 
        method (method, period, null); 
    }
    
    public void method (Method method, WUser target) { 
        method (method, PeriodEntity.getSystem (), target); 
    }
    
    public void method (Method method) { 
        method (method, PeriodEntity.getSystem ()); 
    }
    
    public void group (GroupEntity group) {
        final WUser user = fetchContextUser ();
        
        // Everything is allowed for administrator accounts
        if (user.getEntity ().getIsAdmin ()) { return; }
        
        if (!groupAssignmentsService.isUserInGroup_ss (user, group)) {
            String message = "User has no access to group";
            throw new IllegalStateException (message);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void page (Method controllerMethod, String handle) {
        //method (controllerMethod, PeriodEntity.getSystem (), null);
    }
    
    public void page (Method controllerMethod, String handle, PeriodEntity period) {
        //method (controllerMethod, period, null);
    }
    
}
