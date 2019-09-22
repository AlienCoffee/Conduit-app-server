package ru.shemplo.conduit.appserver.services;

import static java.util.Comparator.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.FileEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetAttemptStatus;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetEntity;
import ru.shemplo.conduit.appserver.entities.repositories.OlympiadAttemptEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.NotEffectiveMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlympiadAttemptsService extends AbsCachedService <SheetAttemptEntity> {
    
    private final OlympiadAttemptEntityRepository olympiadAttemptsRepository;
    private final UsersService usersService;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected SheetAttemptEntity loadEntity (Long id) {
        return olympiadAttemptsRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    @ProtectedMethod
    public SheetAttemptEntity getAttempt (Long id) {
        SheetAttemptEntity entity = getEntity (id);
        
        PeriodEntity period = entity.getOlympiad ().getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period);
        return entity;
    }
    
    @ProtectedMethod @NotEffectiveMethod ({"use cache"})
    public List <SheetAttemptEntity> getUserAttempts (WUser user, SheetEntity olympiad) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        return olympiadAttemptsRepository.findByUserAndOlympiad (user.getEntity (), olympiad);
    }
    
    @ProtectedMethod
    public int getRemainingUserAttemptsNumber (WUser user, SheetEntity olympiad) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        final List <SheetAttemptEntity> attempts = olympiadAttemptsRepository
            . findByUserAndOlympiad (user.getEntity (), olympiad);
        attempts.sort (Comparator.comparing (SheetAttemptEntity::getIssued));
        if (attempts.size () == 0) { return olympiad.getAttemptsLimit (); }
        
        int number = attempts.size ();
        if (SheetAttemptStatus.CHECKED.equals (attempts.get (number - 1).getStatus ())) {
            return 0; // Because there is attempt that is already checked
        }
        
        return Math.max (0, olympiad.getAttemptsLimit () - number);
    }
    
    @ProtectedMethod @Transactional
    public SheetAttemptEntity createAttempt (WUser user, SheetEntity olympiad, FileEntity archive) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        if (getRemainingUserAttemptsNumber (user, olympiad) == 0) {
            String message = "The number of available attempts is exceeded";
            throw new IllegalStateException (message);
        }
        
        SheetAttemptEntity entity = new SheetAttemptEntity ();
        entity.setStatus (SheetAttemptStatus.PENDING);
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommitter (user.getEntity ());
        entity.getAttachments ().add (archive);
        entity.setUser (user.getEntity ());
        entity.setOlympiad (olympiad);
        entity.setComment ("");
        
        log.info (entity.toTemplateString ());
        olympiadAttemptsRepository.rejectAllPreviousAttempts (user.getEntity (), olympiad);
        return olympiadAttemptsRepository.save (entity);
    }
    
    @ProtectedMethod @NotEffectiveMethod ({"use cache"})
    public List <SheetAttemptEntity> getAttemptsForCheck (SheetEntity olympiad) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period);
        
        final List <SheetAttemptEntity> attempts = olympiadAttemptsRepository
            . findByOlympiadAndStatus (olympiad, SheetAttemptStatus.PENDING);
        attempts.sort (comparing (SheetAttemptEntity::getIssued).reversed ());
        
        boolean isOlympiadOver = !LocalDateTime.now ().isBefore (olympiad.getFinished ());
        final List <SheetAttemptEntity> result = new ArrayList <> ();
        final Set <UserEntity> handledUsers = new HashSet <> ();
        for (SheetAttemptEntity attempt : attempts) {
            UserEntity author = attempt.getUser ();
            if (handledUsers.contains (author)) {
                continue;
            }
            
            final WUser wauthor = usersService.getUser (author.getId ());
            if (isOlympiadOver || getRemainingUserAttemptsNumber (wauthor, olympiad) == 0) {
                handledUsers.add (attempt.getUser ());
                result.add (attempt);
            }
        }
        
        return result;
    }
    
    @ProtectedMethod @Transactional
    public void markPendingAttemptsAsChecked (SheetEntity olympiad, WUser committer) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period);
        
        olympiadAttemptsRepository.markAllPendingAttemptsAsChecked (committer.getEntity (), olympiad);
    }
    
    @ProtectedMethod @Transactional
    public void markCheckedAttemptsAsPending (SheetEntity olympiad, WUser committer) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period);
        
        olympiadAttemptsRepository.markAllCheckedAttemptsAsPending (committer.getEntity (), olympiad);
    }
    
}
