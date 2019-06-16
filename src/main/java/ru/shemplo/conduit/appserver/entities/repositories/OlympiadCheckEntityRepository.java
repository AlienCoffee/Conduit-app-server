package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadCheckEntity;

public interface OlympiadCheckEntityRepository extends AbsEntityRepository <OlympiadCheckEntity> {
    
    @Query ("SELECT ent.problem.id FROM OlympiadCheckEntity ent WHERE ent.attempt.id = :attempt")
    public Set <Long> findCheckedProblemsIds (@Param ("attempt") Long attemptID);
    
    @Query ("SELECT ent.problem.id FROM OlympiadCheckEntity ent WHERE ent.attempt.id = :attempt AND ent.committer.id = :user")
    public Set <Long> findCheckedProblemsIdsByUser (@Param ("attempt") Long attemptID, @Param ("user") Long userID);
    
    @Query ("SELECT SUM (ent.points) FROM OlympiadCheckEntity ent WHERE ent.attempt.id = :attempt AND ent.committer.id = :user")
    public Integer getTotalScoreForAttemptByUser (@Param ("attempt") Long attemptID, @Param ("user") Long userID);
    
    public OlympiadCheckEntity findByAttemptAndCommitterAndProblem_Id (OlympiadAttemptEntity attempt, 
            UserEntity committer, Long problem);
    
}
