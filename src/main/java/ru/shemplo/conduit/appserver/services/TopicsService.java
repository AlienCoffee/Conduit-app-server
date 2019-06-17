package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.topics.TopicEntity;
import ru.shemplo.conduit.appserver.entities.repositories.TopicEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicsService extends AbsCachedService <TopicEntity> {

    private final TopicEntityRepository topicsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected TopicEntity loadEntity (Long id) {
        return null;
    }

    @Override
    protected int getCacheSize () { return 16; }
    
    @ProtectedMethod
    public TopicEntity getTopic (Long id) {
        TopicEntity entity = getEntity (id);
        
        PeriodEntity period = entity.getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period);
        return entity;
    }
    
    @ProtectedMethod
    public TopicEntity createTopic (String name, String description, LocalDateTime start, 
            LocalDateTime finish, GroupEntity group, WUser committer) {
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod ());
        
        TopicEntity entity = new TopicEntity ();
        entity.setDescription (description);
        entity.setPublished (start);
        entity.setFinished (finish);
        entity.setGroup (group);
        entity.setName (name);
        
        entity.setCommitter (committer.getEntity ());
        entity.setIssued (LocalDateTime.now (clock));
        
        log.info (entity.toTemplateString ());
        return topicsRepository.save (entity);
    }
    
}
