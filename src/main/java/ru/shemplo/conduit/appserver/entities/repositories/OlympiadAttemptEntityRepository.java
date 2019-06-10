package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;

public interface OlympiadAttemptEntityRepository extends AbsEntityRepository <OlympiadAttemptEntity> {
    
    public List <OlympiadAttemptEntity> findByCommitterAndOlympiad (UserEntity committer, OlympiadEntity olympiad); 
    
}
