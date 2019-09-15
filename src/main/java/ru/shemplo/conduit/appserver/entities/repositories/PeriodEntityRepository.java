package ru.shemplo.conduit.appserver.entities.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;

public interface PeriodEntityRepository extends AbsEntityRepository <PeriodEntity> { 
    
    @Query ("SELECT ent.id FROM PeriodEntity ent")
    public List <Long> findAllIds ();
    
    @Query ("SELECT ent.id FROM PeriodEntity ent WHERE (ent.available IS TRUE) "
            + "AND (ent.until IS NULL OR ent.until > :moment)")
    public List <Long> findAllIdsOfAvailablePeriod (@Param ("moment") LocalDateTime moment);
    
    public PeriodEntity findByName (String name);
    
}
