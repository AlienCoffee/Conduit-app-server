package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptStatus;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;

public interface OlympiadAttemptEntityRepository extends AbsEntityRepository <OlympiadAttemptEntity> {
    
    public List <OlympiadAttemptEntity> findByUserAndOlympiad (UserEntity user, 
            OlympiadEntity olympiad); 
    
    public List <OlympiadAttemptEntity> findByOlympiadAndStatus (OlympiadEntity olympiad, 
            OlympiadAttemptStatus status);
    
    @Modifying @Query ("UPDATE OlympiadAttemptEntity ent "
                       + "SET ent.status='REJECTED', ent.reason='New attempt sent' "
                       + "WHERE ent.user = :user AND ent.olympiad = :olympiad")
    public void rejectAllPreviousAttempts (@Param ("user") UserEntity user, 
            @Param ("olympiad") OlympiadEntity olympiad);
    
}
