package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
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
        
        final GroupType gType = group.getType ();
        if (GroupType.POOL.equals (gType) || GroupType.INFO.equals (gType)) {
            String message = "Olympiad can't be created in " + gType + " group";
            throw new IllegalArgumentException (message);
        }
        
        OlympiadEntity entity = new OlympiadEntity ();
        entity.setDescription (description);
        entity.setAttemptsLimit (attempts);
        entity.setPublished (publish);
        entity.setFinished (finish);
        entity.setGroup (group);
        entity.setName (name);
        
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommitter (creator.getEntity ());
        
        return olympiadsRepository.save (entity);
    }
    
}
