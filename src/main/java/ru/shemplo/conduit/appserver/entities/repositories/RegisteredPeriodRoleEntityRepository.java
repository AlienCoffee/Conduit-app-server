package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.entities.data.RegisteredPeriodRoleEntity;

public interface RegisteredPeriodRoleEntityRepository extends AbsEntityRepository <RegisteredPeriodRoleEntity> {
    
    @Query ("SELECT id FROM RegisteredPeriodRoleEntity")
    public List <Long> findAllIds ();
    
    public RegisteredPeriodRoleEntity findByUserAndPeriodAndTemplate (UserEntity user, 
            PeriodEntity period, PersonalDataTemplate template);
    
    public List <RegisteredPeriodRoleEntity> findByPeriodAndTemplate (PeriodEntity period, 
            PersonalDataTemplate template);
    
    @Query ("SELECT item.template FROM RegisteredPeriodRoleEntity item WHERE item.period = :period AND item.user = :user")
    public List <PersonalDataTemplate> findTempltesByPeriodAndUser (PeriodEntity period, UserEntity user);
    
}
