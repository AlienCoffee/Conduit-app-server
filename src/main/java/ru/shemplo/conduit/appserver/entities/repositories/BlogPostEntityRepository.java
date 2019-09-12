package ru.shemplo.conduit.appserver.entities.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.shemplo.conduit.appserver.entities.BlogPostEntity;

public interface BlogPostEntityRepository extends AbsEntityRepository <BlogPostEntity> {
    
    @Query ("SELECT ent.id FROM BlogPostEntity ent WHERE ent.published <= :date")
    public List <Long> findIdsBeforeDate (@Param ("date") LocalDateTime date);
    
    @Query ("SELECT ent.id FROM BlogPostEntity ent JOIN ent.channels chs WHERE (:channel IN chs) "
            + "AND (ent.published < :date) AND (ent.available = TRUE)")
    public List <Long> findIdsBeforeDateInChannel (@Param ("channel") String channel, @Param ("date") LocalDateTime date, 
            Pageable pageable);
    
}
