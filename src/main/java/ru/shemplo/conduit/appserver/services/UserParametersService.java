package ru.shemplo.conduit.appserver.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.UserParameter;
import ru.shemplo.conduit.appserver.entities.UserParameterName;
import ru.shemplo.conduit.appserver.entities.repositories.UserParameterRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class UserParametersService extends AbsCachedService <UserParameter> {

    private final UserParameterRepository userParameterRepository;
    private final AccessGuard accessGuard;
    
    @Override
    protected UserParameter loadEntity (Long id) {
        return userParameterRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () {
        return 64;
    }
    
    @ProtectedMethod
    public UserParameter getParameterByName (WUser user, UserParameterName paramter) {
        accessGuard.method (MiscUtils.getMethod (), user);
        
        var entId = userParameterRepository.findIdByUser_IdAndParameter (
            user.getId (), paramter
        );
        return entId != null ? getEntity (entId) : null;
    }
    
    @ProtectedMethod
    public UserParameter setParameterValue (WUser user, UserParameterName parameter, String value) {
        accessGuard.method (MiscUtils.getMethod (), user);
        
        UserParameter entity = getParameterByName (user, parameter);
        if (entity == null) {
            entity = new UserParameter (user.getEntity (), parameter, value);
        } else {            
            CACHE.invalidate (entity.getId ());
        }
        
        entity.setValue (value);
        return userParameterRepository.save (entity);
    }
    
}
