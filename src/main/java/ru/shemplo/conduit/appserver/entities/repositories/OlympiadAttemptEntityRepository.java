package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetAttemptStatus;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetEntity;

public interface OlympiadAttemptEntityRepository extends AbsEntityRepository <SheetAttemptEntity> {
    
    public List <SheetAttemptEntity> findByUserAndOlympiad (UserEntity user, 
            SheetEntity olympiad); 
    
    public List <SheetAttemptEntity> findByOlympiadAndStatus (SheetEntity olympiad, 
            SheetAttemptStatus status);
    
    @Modifying @Query ("UPDATE OlympiadAttemptEntity ent "
                       + "SET ent.status='REJECTED', ent.reason='New attempt sent' "
                       + "WHERE ent.user = :user AND ent.olympiad = :olympiad")
    public void rejectAllPreviousAttempts (@Param ("user") UserEntity user, 
            @Param ("olympiad") SheetEntity olympiad);
    
    @Modifying @Query ("UPDATE OlympiadAttemptEntity ent SET ent.status='CHECKED', "
                       + "ent.reason='Olympiad results finalized', ent.committer = :user "
                       + "WHERE ent.olympiad = :olympiad AND ent.status='PENDING'")
    public void markAllPendingAttemptsAsChecked (@Param ("user") UserEntity user, 
        @Param ("olympiad") SheetEntity olympiad);
    
    @Modifying @Query ("UPDATE OlympiadAttemptEntity ent SET ent.status='PENDING', "
            + "ent.reason='Olympiad results invalidated', ent.committer = :user "
            + "WHERE ent.olympiad = :olympiad AND ent.status='CHECKED'")
    public void markAllCheckedAttemptsAsPending (@Param ("user") UserEntity user, 
        @Param ("olympiad") SheetEntity olympiad);
    
}
