package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;

public interface PeriodEntityRepository extends AbsEntityRepository <PeriodEntity> { 
    
    @Query ("SELECT id FROM PeriodEntity")
    public List <Long> findAllIds ();
    
    public PeriodEntity findByName (String name);
    
}
