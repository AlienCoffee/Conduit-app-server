package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.OptionEntity;

public interface OptionEntityRepository extends AbsEntityRepository <OptionEntity> {
    
    @Query ("SELECT id FROM OptionEntity")
    public List <Long> findAllIds ();
    
    public OptionEntity findByName (String name);
    
}
