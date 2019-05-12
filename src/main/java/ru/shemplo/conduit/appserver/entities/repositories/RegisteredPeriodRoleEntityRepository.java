package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.entities.data.RegisteredPeriodRoleEntity;

public interface RegisteredPeriodRoleEntityRepository extends AbsEntityRepository <RegisteredPeriodRoleEntity> {
    
    public RegisteredPeriodRoleEntity findByUserAndPeriodAndTemplate (UserEntity user, 
            PeriodEntity period, PersonalDataTemplate template);
    
}
