package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.BlogPostEntity;
import ru.shemplo.conduit.appserver.entities.repositories.BlogPostEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPostsService extends AbsCachedService <BlogPostEntity> {

    private final BlogPostEntityRepository blogPostsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected BlogPostEntity loadEntity (Long id) {
        return blogPostsRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    @ProtectedMethod
    public void createBlogPost (String title, String content, 
            LocalDateTime published, WUser author) {
        accessGuard.method (MiscUtils.getMethod ());
        
        BlogPostEntity entity = new BlogPostEntity ();
        entity.setPublished (published);
        entity.setContent (content);
        entity.setTitle (title);
        
        entity.setCommitter (author.getEntity ());
        entity.setIssued (LocalDateTime.now (clock));
        
        log.info (entity.toTemplateString ());
        blogPostsRepository.save (entity);
    }
    
    @ProtectedMethod
    public List <BlogPostEntity> getMainChannelPosts (LocalDateTime till) {
        accessGuard.method (MiscUtils.getMethod ());
        
        final List <Long> ids = blogPostsRepository
            . findIdsBeforeDateInMainChannel (till);
        
        return ids.stream ().map (blogPostsRepository::findById)
             . map     (Optional::get)
             . collect (Collectors.toList ());
    }
    
}
