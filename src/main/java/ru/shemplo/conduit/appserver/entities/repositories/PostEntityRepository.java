package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.PostEntity;

public interface PostEntityRepository extends AbsEntityRepository <PostEntity> {
    
    @Query ("SELECT id FROM PostEntity")
    public List <Long> findAllIds ();
    
    @Query ("SELECT id FROM PostEntity WHERE group_id = :group")
    public List <Long> findIdsByGroup (GroupEntity group);
    
}
