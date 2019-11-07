package ru.shemplo.conduit.appserver.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.UserParameter;
import ru.shemplo.conduit.appserver.entities.repositories.UserParameterRepository;
import ru.shemplo.conduit.appserver.security.AccessGuard;

@Service
@RequiredArgsConstructor
public class UserParametersService extends AbsCachedService <UserParameter> {

    private final UserParameterRepository userParameterRepository;
    @SuppressWarnings ("unused")
    private final AccessGuard accessGuard;
    
    @Override
    protected UserParameter loadEntity (Long id) {
        return userParameterRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () {
        return 64;
    }
    
}
