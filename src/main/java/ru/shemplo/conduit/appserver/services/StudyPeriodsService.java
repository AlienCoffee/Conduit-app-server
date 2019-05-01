package ru.shemplo.conduit.appserver.services;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.repositories.StudyPeriodEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
//@PreAuthorize ("@accessGuard.type (authentication, this)")
public class StudyPeriodsService {
    
    private final StudyPeriodEntityRepository periodsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    public Collection <PeriodEntity> getAllPeriods () {
        accessGuard.method (MiscUtils.getMethod ());
        return periodsRepository.findAll ();
    }
    
    public PeriodEntity getPeriod (long id) throws EntityNotFoundException {
        accessGuard.method (MiscUtils.getMethod ());
        return periodsRepository.findById (id).orElseThrow (
            () -> new EntityNotFoundException (NO_ENTITY_MESSAGE + ": study period")
        );
    }
    
    public PeriodEntity updatePeriod (PeriodEntity entity) {
        return periodsRepository.save (entity);
    }
    
    public PeriodEntity createPeriod (String name, String description, LocalDateTime since, 
            LocalDateTime until, boolean isActive, WUser user) {
        PeriodEntity entity = new PeriodEntity ();
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommiter (user.getEntity ());
        entity.setDescription (description);
        entity.setActive (isActive);
        entity.setSince (since);
        entity.setUntil (until);
        entity.setName (name);
        
        return periodsRepository.save (entity);
    }
    
}
