package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.RoleEntity;

public interface RoleEntityRepository extends AbsEntityRepository <RoleEntity> {
    
    public RoleEntity findByName (String name);
    
}
