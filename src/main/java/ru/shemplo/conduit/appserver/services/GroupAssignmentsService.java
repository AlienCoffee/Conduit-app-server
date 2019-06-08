package ru.shemplo.conduit.appserver.services;

import static ru.shemplo.conduit.appserver.entities.AssignmentStatus.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.AssignmentStatus;
import ru.shemplo.conduit.appserver.entities.groups.GroupAssignmentEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.repositories.GroupAssignmentEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class GroupAssignmentsService extends AbsCachedService <GroupAssignmentEntity> {

    private final GroupAssignmentEntityRepository assignmentRepository;
    private final AccessGuard accessGuard;
    
    @Override
    protected GroupAssignmentEntity loadEntity (Long id) {
        return assignmentRepository.findById (id).get ();
    }

    @Override
    protected int getCacheSize () {
        return 64;
    }
    
    @ProtectedMethod
    public GroupAssignmentEntity getUserStatusForGroup (WUser user, GroupEntity group) {
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod (), user);
        return getEntity (assignmentRepository.findIdByUserAndGroup (user.getEntity (), group));
    }
    
    @ProtectedMethod
    public List <GroupAssignmentEntity> getAllUserApplications (WUser user) {
        accessGuard.method (MiscUtils.getMethod (), user);
        
        final List <Long> ids = assignmentRepository
            . findIdsByUser (user.getEntity ());
        return getEntities (ids, true);
    }
    
    @ProtectedMethod
    public boolean isUserInGroup (WUser user, GroupEntity group) {
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod (), user);
        GroupAssignmentEntity assignment = assignmentRepository
        . findByUserAndGroup (user.getEntity (), group);
        
        final AssignmentStatus status = assignment.getStatus ();
        return assignment != null && AssignmentStatus.ASSIGNED.equals (status);
    }
    
    @ProtectedMethod
    public Set <GroupEntity> getUserGroups (WUser user) {
        accessGuard.method (MiscUtils.getMethod (), user);
        
        return getAllUserApplications (user).stream ()
             . filter  (item -> ASSIGNED.equals (item.getStatus ()))
             . map     (GroupAssignmentEntity::getGroup)
             . collect (Collectors.toSet ());
    }
    
    @ProtectedMethod
    public Set <GroupEntity> getUserApplications (WUser user) {
        accessGuard.method (MiscUtils.getMethod (), user);
        
        return getAllUserApplications (user).stream ()
             . filter  (item -> APPLICATION.equals (item.getStatus ()))
             . map     (GroupAssignmentEntity::getGroup)
             . collect (Collectors.toSet ());
    }
    
}
