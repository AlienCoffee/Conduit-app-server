package ru.shemplo.conduit.appserver.services;

import java.util.Collection;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.conduit.appserver.entities.repositories.OptionEntityRepository;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class OptionsService {
    
    private final OptionEntityRepository optionsRepository;
    private final AccessGuard accessGuard;
    //private final Clock clock;
    
    private static final int CACHE_SIZE = 64;
    
    private final LRUCache <OptionEntity> CACHE = new LRUCache <> (CACHE_SIZE);
    
    public OptionEntity createOption (String name) throws EntityExistsException {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (optionsRepository.findByName (name) != null) {
            throw new EntityExistsException ("Option exists");
        }
        
        OptionEntity entity = new OptionEntity (name);
        return optionsRepository.save (entity);
    }
    
    public OptionEntity getOption (long id) {
        OptionEntity option = CACHE.getOrPut (id, 
            () -> optionsRepository.findById (id).get ()
        );
        
        if (option != null) { return option; }
        
        String message = "Unknown credits `" + id + "`";
        throw new EntityNotFoundException (message);
    }
    
    public Collection <OptionEntity> getAllOptions () {
        accessGuard.method (MiscUtils.getMethod ());
        return optionsRepository.findAll ();
    }
    
}
