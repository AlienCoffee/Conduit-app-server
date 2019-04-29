package ru.shemplo.conduit.appserver.services;

import static ru.shemplo.conduit.appserver.ServerConstants.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.StudyPeriodEntity;
import ru.shemplo.conduit.appserver.entities.repositories.StudyPeriodEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;

@Service
@RequiredArgsConstructor
//@PreAuthorize ("@accessGuard.type (authentication, this)")
public class StudyPeriodsService {
    
    private final StudyPeriodEntityRepository periodsRepository;
    private final Clock clock;
    
    //@PreAuthorize ("@accessGuard.method (authentication, this, \"getAllPeriods\")")
    public Collection <StudyPeriodEntity> getAllPeriods () {
        return periodsRepository.findAll ();
    }
    
    public StudyPeriodEntity getPeriod (long id) throws EntityNotFoundException {
        return periodsRepository.findById (id).orElseThrow (
            () -> new EntityNotFoundException (NO_ENTITY_MESSAGE + ": study period")
        );
    }
    
    public StudyPeriodEntity updatePeriod (StudyPeriodEntity entity) {
        return periodsRepository.save (entity);
    }
    
    public StudyPeriodEntity createPeriod (String name, String description, LocalDateTime since, 
            LocalDateTime until, boolean isActive, WUser user) {
        StudyPeriodEntity entity = new StudyPeriodEntity ();
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
