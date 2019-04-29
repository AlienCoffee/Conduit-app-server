package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.UserEntity;

public interface UserEntityRepository extends AbsEntityRepository <UserEntity> {
    
    public UserEntity findByPhone (String phone);
    
    public UserEntity findByLogin (String phone);
    
}
