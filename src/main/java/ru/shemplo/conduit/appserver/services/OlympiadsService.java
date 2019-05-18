package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupType;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.repositories.OlympiadEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class OlympiadsService {
    
    private final OlympiadEntityRepository olympiadsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    private static final int CACHE_SIZE = 32;
    
    private final LRUCache <OlympiadEntity> CACHE = new LRUCache <> (CACHE_SIZE);
    
    @ProtectedMethod
    public OlympiadEntity getOlympiad (long id) throws EntityNotFoundException {
        accessGuard.method (MiscUtils.getMethod ());
        
        OlympiadEntity period = CACHE.getOrPut (id, 
            () -> olympiadsRepository.findById (id).orElse (null)
        );
        
        if (period != null) { return period; }
        
        String message = "Unknown olympiad credits `" + id + "`";
        throw new EntityNotFoundException (message);
    }
    
    @ProtectedMethod
    public List <OlympiadEntity> getOlympiadsByGroup (GroupEntity group) {
        accessGuard.method (MiscUtils.getMethod ());
        
        return olympiadsRepository.findIdsByGroup (group).stream ()
             . map     (this::getOlympiad)
             . collect (Collectors.toList ());
    }
    
    @ProtectedMethod
    public OlympiadEntity createOlympiad (GroupEntity group, String name, String description, 
            LocalDateTime publish, LocalDateTime finish, Integer attempts, WUser creator) {
        accessGuard.method (MiscUtils.getMethod ());
        
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
        
        return olympiadsRepository.save (entity);
    }
    
}
