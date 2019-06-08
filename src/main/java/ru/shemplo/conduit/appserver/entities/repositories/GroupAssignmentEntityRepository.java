package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupAssignmentEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

public interface GroupAssignmentEntityRepository extends AbsEntityRepository <GroupAssignmentEntity> {
    
    public GroupAssignmentEntity findByUserAndGroup (UserEntity user, GroupEntity group);
    
    @Query ("SELECT ent.id FROM GroupAssignmentEntity ent WHERE ent.user = :user AND ent.group = :group")
    public Long findIdByUserAndGroup (UserEntity user, GroupEntity group);
    
    public List <GroupAssignmentEntity> findByGroup (GroupEntity group);
    
    @Query ("SELECT ent.id FROM GroupAssignmentEntity ent WHERE ent.group = :group")
    public List <Long> findIdsByGroup (GroupEntity group);
    
    public List <GroupAssignmentEntity> findByUser (UserEntity user);
    
    @Query ("SELECT ent.id FROM GroupAssignmentEntity ent WHERE ent.user = :user")
    public List <Long> findIdsByUser (UserEntity user);
    
}
