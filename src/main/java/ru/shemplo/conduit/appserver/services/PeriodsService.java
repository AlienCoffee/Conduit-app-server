package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.PeriodStatus;
import ru.shemplo.conduit.appserver.entities.repositories.PeriodEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodsService extends AbsCachedService <PeriodEntity> {
    
    private final PeriodEntityRepository periodsRepository;
    @Autowired private AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected PeriodEntity loadEntity (Long id) {
        return periodsRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    public PeriodEntity getPeriod_ss (Long id) throws EntityNotFoundException {
        return getEntity (id);
    }
    
    @ProtectedMethod
    public PeriodEntity getPeriod (Long id) {
        accessGuard.method (MiscUtils.getMethod ());
        return getEntity (id);
    }
    
    @ProtectedMethod
    public Collection <PeriodEntity> getAllPeriods () {
        accessGuard.method (MiscUtils.getMethod ());
        
        List <Long> ids = periodsRepository.findAllIds ();
        return getEntities (ids, true);
    }
    
    @ProtectedMethod
    public Collection <PeriodEntity> getAllAvailablePeriods (LocalDateTime moment) {
        accessGuard.method (MiscUtils.getMethod ());
        
        List <Long> ids = periodsRepository.findAllIdsOfAvailablePeriod (moment);
        return getEntities (ids, true);
    }
    
    @ProtectedMethod
    public PeriodEntity createPeriod (String name, String description, LocalDateTime since, 
            LocalDateTime until, PeriodStatus status, WUser user) {
        accessGuard.method (MiscUtils.getMethod ());
        
        final PeriodEntity entity = new PeriodEntity ();
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommitter (user.getEntity ());
        entity.setDescription (description);
        entity.setStatus (status);
        entity.setSince (since);
        entity.setUntil (until);
        entity.setName (name);
        
        log.info (entity.toTemplateString ());
        return periodsRepository.save (entity);
    }
    
    @ProtectedMethod
    public PeriodEntity changePeriodStatus (PeriodEntity period, 
            PeriodStatus status, WUser committer) {
        accessGuard.method (MiscUtils.getMethod (), period);
        if (PeriodStatus.CREATED.equals (status)) {
            String message = "Period status can't be switched to " 
                           + PeriodStatus.CREATED;
            throw new IllegalStateException (message);
        }
        
        period.setCommitter (committer.getEntity ());
        period.setIssued (LocalDateTime.now (clock));
        
        period.setStatus (status);
        
        log.info (period.toTemplateString ());
        return periodsRepository.save (period);
    }
    
}
