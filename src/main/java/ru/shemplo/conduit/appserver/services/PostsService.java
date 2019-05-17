package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.PostEntity;
import ru.shemplo.conduit.appserver.entities.repositories.PostEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class PostsService {
    
    private final PostEntityRepository postsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    private static final int CACHE_SIZE = 32;
    
    private final LRUCache <PostEntity> CACHE = new LRUCache <> (CACHE_SIZE);
    
    @ProtectedMethod
    public PostEntity getPost (long id) throws EntityNotFoundException {
        accessGuard.method (MiscUtils.getMethod ());
        
        PostEntity period = CACHE.getOrPut (id, 
            () -> postsRepository.findById (id).orElse (null)
        );
        
        if (period != null) { return period; }
        
        String message = "Unknown post credits `" + id + "`";
        throw new EntityNotFoundException (message);
    }
    
    @ProtectedMethod
    public PostEntity createInforamtionPost (GroupEntity group, String title, 
            String content, LocalDateTime published, WUser author) {
        accessGuard.method (MiscUtils.getMethod ());
        
        PostEntity post = new PostEntity (group, title, content, 
                                published, new ArrayList <> ());
        post.setIssued (LocalDateTime.now (clock));
        post.setCommitter (author.getEntity ());
        
        return postsRepository.save (post);
    }
    
}
