package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

public interface GroupEntityRepository extends AbsEntityRepository <GroupEntity> {
    
    @Query ("SELECT ent.id FROM GroupEntity ent")
    public List <Long> findAllIds ();
    
    @Query ("SELECT ent.id FROM GroupEntity ent WHERE ent.period = :period")
    public List <Long> findIdsByPeriod (PeriodEntity period);
    
    public List <GroupEntity> findByPeriod (PeriodEntity period);
    
}
