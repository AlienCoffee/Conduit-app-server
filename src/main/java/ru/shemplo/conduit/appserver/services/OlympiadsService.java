package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupType;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.repositories.OlympiadEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.NotEffectiveMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlympiadsService extends AbsCachedService <OlympiadEntity> {
    
    private final OlympiadAttemptsService olympiadAttemptsService;
    private final OlympiadEntityRepository olympiadsRepository;
    private final OlympaidChecksService olympaidChecksService;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected OlympiadEntity loadEntity (Long id) {
        return olympiadsRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    @ProtectedMethod
    public OlympiadEntity getOlympiad (long id) throws EntityNotFoundException {
        OlympiadEntity olympiad = getEntity (id);
        if (olympiad == null) { return null; }
        
        PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period);
        return olympiad;
    }
    
    @ProtectedMethod
    public List <OlympiadEntity> getOlympiadsByGroup (GroupEntity group) {
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod ());
        
        return olympiadsRepository.findIdsByGroup (group).stream ()
             . map     (this::getOlympiad)
             . collect (Collectors.toList ());
    }
    
    @ProtectedMethod
    public OlympiadEntity createOlympiad (GroupEntity group, String name, String description, 
            LocalDateTime publish, LocalDateTime finish, Integer attempts, WUser creator) {
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod ());
        
        Objects.requireNonNull (publish, "Publish time should be set up");
        
        final GroupType gType = group.getType ();
        if (GroupType.POOL.equals (gType) || GroupType.INFO.equals (gType)) {
            String message = "Olympiad can't be created in " + gType + " group";
            throw new IllegalArgumentException (message);
        }
        
        final LocalDateTime now = LocalDateTime.now (clock);
        if (publish.isBefore (now)) {
            String message = "Olympiad publish time can't be before current time";
            throw new IllegalArgumentException (message);
        }
        
        OlympiadEntity entity = new OlympiadEntity ();
        if (attempts != null) {
            entity.setAttemptsLimit (attempts);            
        }
        entity.setDescription (description);
        entity.setPublished (publish);
        entity.setFinished (finish);
        entity.setGroup (group);
        entity.setName (name);
        
        entity.setCommitter (creator.getEntity ());
        entity.setIssued (now);
        
        log.info (entity.toTemplateString ());
        return olympiadsRepository.save (entity);
    }
    
    @ProtectedMethod @NotEffectiveMethod
    public OlympiadEntity setResultsStatus (OlympiadEntity olympiad, boolean finallized, WUser committer) {
        final PeriodEntity period = olympiad.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period);
        
        if (finallized) {
            finalizeOlympiadResults (olympiad, committer);
            String message = "Results finalized";
            olympiad.setComment (message);
        } else {
            String message = "Results invalidated and check openned again";
            invalidateOlympiadResults (olympiad, committer);
            olympiad.setComment (message);
        }
        
        olympiad.setCommitter (committer.getEntity ());
        olympiad.setIssued (LocalDateTime.now (clock));
        olympiad.setResultsFinalized (finallized);
        
        return olympiadsRepository.save (olympiad);
    }
    
    private void finalizeOlympiadResults (OlympiadEntity olympiad, WUser committer) {
        if (LocalDateTime.now (clock).isBefore (olympiad.getFinished ())) {
            String message = "Results can't be finalized before the end of olympiad";
            throw new IllegalStateException (message);
        }
        
        List <OlympiadAttemptEntity> attempts = olympiadAttemptsService
           . getAttemptsForCheck (olympiad);
        for (OlympiadAttemptEntity attempt : attempts) {
            if (!olympaidChecksService.isAttemptChecked (attempt)) {
                String message = "At least one attempt is not checked (attempt: " 
                               + attempt.getId () + ")";
                throw new IllegalStateException (message);
            }
        }
        
        olympiadAttemptsService.markPendingAttemptsAsChecked (olympiad, committer);
    }
    
    private void invalidateOlympiadResults (OlympiadEntity olympiad, WUser committer) {
        olympiadAttemptsService.markCheckedAttemptsAsPending (olympiad, committer);
    }
    
}
