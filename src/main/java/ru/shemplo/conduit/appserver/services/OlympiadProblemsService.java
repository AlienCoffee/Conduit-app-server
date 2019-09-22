package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetEntity;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetProblemEntity;
import ru.shemplo.conduit.appserver.entities.repositories.OlympiadProblemEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class OlympiadProblemsService {
    
    private final OlympiadProblemEntityRepository problemsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    private static final int CACHE_SIZE = 128;
    
    private final LRUCache <SheetProblemEntity> CACHE = new LRUCache <> (CACHE_SIZE);
    
    @ProtectedMethod
    public SheetProblemEntity getProblem (long id) throws EntityNotFoundException {
        accessGuard.method (MiscUtils.getMethod ());
        
        SheetProblemEntity period = CACHE.getOrPut (id, 
            () -> problemsRepository.findById (id).orElse (null)
        );
        
        if (period != null) { return period; }
        
        String message = "Unknown problem credits `" + id + "`";
        throw new EntityNotFoundException (message);
    }
    
    @ProtectedMethod
    public List <SheetProblemEntity> getProblemsByOlympiad (SheetEntity olympiad) {
        accessGuard.method (MiscUtils.getMethod ());
        
        return problemsRepository.findIdsByOlympiad (olympiad).stream ()
             . map     (this::getProblem)
             . collect (Collectors.toList ());
    }
    
    @ProtectedMethod
    public SheetProblemEntity createOlympiadProblem (SheetEntity olympiad, 
            String title, String content, Integer cost, Integer difficulty, 
            WUser author) {
        accessGuard.method (MiscUtils.getMethod ());
        
        final SheetProblemEntity entity = new SheetProblemEntity ();
        entity.setDifficulty (difficulty != null ? difficulty.doubleValue () : null);
        entity.setOlympiad (olympiad);
        entity.setContent (content);
        entity.setTitle (title);
        entity.setCost (cost);
        
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommitter (author.getEntity ());
        
        return problemsRepository.save (entity);
    }
    
}
