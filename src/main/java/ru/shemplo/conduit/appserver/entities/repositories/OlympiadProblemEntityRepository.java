package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadProblemEntity;

public interface OlympiadProblemEntityRepository extends AbsEntityRepository <OlympiadProblemEntity> {
    
    @Query ("SELECT id FROM OlympiadProblemEntity")
    public List <Long> findAllIds ();
    
    @Query ("SELECT id FROM OlympiadProblemEntity WHERE olympiad_id = :olympiad")
    public List <Long> findIdsByOlympiad (OlympiadEntity olympiad);
    
}
