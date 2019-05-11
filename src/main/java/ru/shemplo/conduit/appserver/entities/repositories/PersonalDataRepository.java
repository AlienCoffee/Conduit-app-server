package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataType;

public interface PersonalDataRepository extends AbsEntityRepository <PersonalDataEntity> {
    
    public PersonalDataEntity findByUserAndPeriodAndType (UserEntity user, PeriodEntity period, PersonalDataType type);
    
}
