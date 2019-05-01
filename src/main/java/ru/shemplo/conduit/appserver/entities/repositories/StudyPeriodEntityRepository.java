package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;

public interface StudyPeriodEntityRepository extends AbsEntityRepository <PeriodEntity> {
    
    public PeriodEntity findByName (String name);
    
}
