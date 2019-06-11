package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.FileEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptStatus;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.repositories.OlympiadAttemptEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlympiadAttemptsService extends AbsCachedService <OlympiadAttemptEntity> {
    
    private final OlympiadAttemptEntityRepository olympiadAttemptsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected OlympiadAttemptEntity loadEntity (Long id) {
        return olympiadAttemptsRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    @ProtectedMethod
    public int getRemainingUserAttemptsNumber (WUser user, OlympiadEntity olympiad) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        final List <OlympiadAttemptEntity> attempts = olympiadAttemptsRepository
            . findByCommitterAndOlympiad (user.getEntity (), olympiad);
        attempts.sort (Comparator.comparing (OlympiadAttemptEntity::getIssued));
        if (attempts.size () == 0) { return olympiad.getAttemptsLimit (); }
        
        int number = attempts.size ();
        if (OlympiadAttemptStatus.CHECKED.equals (attempts.get (number - 1).getStatus ())) {
            return 0; // Because there is attempt that is already checked
        }
        
        return Math.max (0, olympiad.getAttemptsLimit () - number);
    }
    
    @ProtectedMethod
    public OlympiadAttemptEntity createAttempt (WUser user, OlympiadEntity olympiad, FileEntity archive) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        if (getRemainingUserAttemptsNumber (user, olympiad) == 0) {
            String message = "The number of available attempts is exceeded";
            throw new IllegalStateException (message);
        }
        
        OlympiadAttemptEntity entity = new OlympiadAttemptEntity ();
        entity.setStatus (OlympiadAttemptStatus.PENDING);
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommitter (user.getEntity ());
        entity.getAttachments ().add (archive);
        entity.setOlympiad (olympiad);
        entity.setComment ("");
        
        log.info (entity.toTemplateString ());
        return olympiadAttemptsRepository.save (entity);
    }
    
}
