package ru.shemplo.conduit.appserver.services;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.repositories.RoleEntityRepository;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class RolesService {
    
    private final RoleEntityRepository rolesRepository;
    private final AccessGuard accessGuard;
    
    private static final int CACHE_SIZE = 32;
    
    private final LRUCache <RoleEntity> CACHE = new LRUCache <> (CACHE_SIZE);
    
    @ProtectedMethod
    public RoleEntity createRole (String name) {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (rolesRepository.findByName (name) != null) {
            throw new EntityExistsException ("Role exists");
        }
        
        RoleEntity entity = new RoleEntity (name, new HashSet <> ());
        return rolesRepository.save (entity);
    }
    
    public RoleEntity getRole (long id) {
        RoleEntity role = CACHE.getOrPut (id, 
            () -> rolesRepository.findById (id).orElse (null)
        );
        
        if (role != null) { return role; }
        
        String message = "Unknown role credits `" + id + "`";
        throw new EntityNotFoundException (message);
    }
    
    @ProtectedMethod
    public Collection <RoleEntity> getAllRoles () {
        accessGuard.method (MiscUtils.getMethod ());
        return rolesRepository.findAll ();
    }
    
    @ProtectedMethod
    public RoleEntity addOptionToRole (RoleEntity role, OptionEntity option) {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (role.getOptions ().add (option)) {            
            CACHE.invalidate (role.getId ());
        }
        
        return rolesRepository.save (role);
    }
    
    @ProtectedMethod
    public RoleEntity removeOptionFromRole (RoleEntity role, OptionEntity option) {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (role.getOptions ().remove (option)) {            
            CACHE.invalidate (role.getId ());
        }
        
        return rolesRepository.save (role);
    }
    
}
