package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataField;

public interface PersonalDataRepository extends AbsEntityRepository <PersonalDataEntity> {
    
    public List <PersonalDataEntity> findByUserAndPeriod (UserEntity user, PeriodEntity period);
    
    public PersonalDataEntity findByUserAndPeriodAndField (UserEntity user, 
            PeriodEntity period, PersonalDataField field);
    
}
