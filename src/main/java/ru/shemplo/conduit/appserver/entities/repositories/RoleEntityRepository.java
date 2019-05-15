package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.RoleEntity;

public interface RoleEntityRepository extends AbsEntityRepository <RoleEntity> {
    
    @Query ("SELECT id FROM RoleEntity")
    public List <Long> findAllIds ();
    
    public RoleEntity findByName (String name);
    
}
