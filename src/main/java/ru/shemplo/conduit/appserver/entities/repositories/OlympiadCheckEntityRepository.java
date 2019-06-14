package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadCheckEntity;

public interface OlympiadCheckEntityRepository extends AbsEntityRepository <OlympiadCheckEntity> {
    
    @Query ("SELECT ent.id FROM OlympiadCheckEntity ent WHERE ent.attempt = :attempt")
    public Set <Long> findCheckedProblemsIds (@Param ("attempt") OlympiadAttemptEntity attempt);
    
}
