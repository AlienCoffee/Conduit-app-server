package ru.shemplo.conduit.appserver.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.*;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.SecurityService.SecurityEntity;
import ru.shemplo.conduit.appserver.services.AbsCachedService;
import ru.shemplo.conduit.appserver.services.PeriodsService;
import ru.shemplo.conduit.appserver.services.RolesService;
import ru.shemplo.conduit.appserver.services.UsersService;
import ru.shemplo.conduit.appserver.utils.Utils;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.stuctures.Trio;

@Service
@RequiredArgsConstructor
public class SecurityService extends AbsCachedService <SecurityEntity> {
    
    private final PeriodsService periodsService;
    private final RolesService rolesService;
    private final UsersService usersService;
    
    protected class SecurityEntity extends Trio <PeriodEntity, UserEntity, Set <OptionEntity>> implements Identifiable {

        public SecurityEntity (PeriodEntity F, UserEntity S, Set <OptionEntity> T) { super (F, S, T); }
        
        @Getter private final Long id = Utils.hash2 (F, S);
        
    }
    
    @Override
    protected SecurityEntity loadEntity (Long id) {
        Pair <Long, Long> periodNuserIds = Utils.dehash2 (id);
        final PeriodEntity period = periodsService.getPeriod_ss (periodNuserIds.F);
        final WUser user = usersService.getUser_ss (periodNuserIds.S);
        
        final List <Long> ids = rolesService.getUserRolesIds_ss (period, user.getEntity ());
        Set <OptionEntity> options = rolesService.getRoles_ss (ids).stream ()
                                   . map     (RoleEntity::getOptions)
                                   . flatMap (Set::stream)
                                   . collect (Collectors.toSet ());
        return new SecurityEntity (period, user.getEntity (), options);
    }

    @Override
    protected int getCacheSize () { return 128; }
    
    public Set <OptionEntity> getUserOptionsForPeriod (PeriodEntity period, UserEntity user) {
        return getEntity (Utils.hash2 (period, user)).getT ();
    }
    
    public void invalidateForUserInPeriod (UserEntity user, PeriodEntity period) {
        System.out.println ("Invalidation of " + user.getLogin () + " in " + period.getName ());
        CACHE.invalidate (Utils.hash2 (period, user));
    }
    
}
