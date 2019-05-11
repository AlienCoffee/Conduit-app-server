package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataField;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataType;

public interface PersonalDataRepository extends AbsEntityRepository <PersonalDataEntity> {
    
    public List <PersonalDataEntity> findByUserAndPeriodAndType (UserEntity user, 
            PeriodEntity period, PersonalDataType type);
    
    public PersonalDataEntity findByUserAndPeriodAndTypeAndField (UserEntity user, 
            PeriodEntity period, PersonalDataType type, PersonalDataField field);
    
}
