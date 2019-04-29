package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import ru.shemplo.conduit.appserver.entities.GroupEntity;
import ru.shemplo.conduit.appserver.entities.StudyPeriodEntity;

public interface GroupEntityRepository extends AbsEntityRepository <GroupEntity> {
    
    public List <GroupEntity> findByPeriod (StudyPeriodEntity period);
    
}
