package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.UserEntity;

public interface UserEntityRepository extends AbsEntityRepository <UserEntity> {
    
    @Query ("SELECT id FROM UserEntity")
    public List <Long> findAllIds ();
    
    public UserEntity findByPhone (String phone);
    
    public UserEntity findByLogin (String login);
    
}
