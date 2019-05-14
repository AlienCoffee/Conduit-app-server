package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

public interface GroupEntityRepository extends AbsEntityRepository <GroupEntity> {
    
    public List <GroupEntity> findByPeriod (PeriodEntity period);
    
}
