package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupAssignmentEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

public interface GroupAssignmentEntityRepository extends AbsEntityRepository <GroupAssignmentEntity> {
    
    public GroupAssignmentEntity findByUserAndGroup (UserEntity user, GroupEntity entity);
    
    public List <GroupAssignmentEntity> findByGroup (GroupEntity group);
    
}
