package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.groups.*;
import ru.shemplo.conduit.appserver.entities.repositories.GroupAssignmentEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.GroupEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.conduit.appserver.web.dto.GroupMember;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class GroupsService {
    
    private final GroupAssignmentEntityRepository gAssignmentsRepository;
    private final GroupEntityRepository groupsRepository;
    private final WUserService usersService;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    private static final int CACHE_SIZE = 64;
    
    private final LRUCache <GroupEntity> CACHE = new LRUCache <> (CACHE_SIZE);
    
    @ProtectedMethod
    public List <GroupEntity> getPeriodGroups (PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod ());
        
        return groupsRepository.findIdsByPeriod (period).stream ()
             . map     (this::getGroup)
             . collect (Collectors.toList ());
    }
    
    @ProtectedMethod
    public GroupEntity getGroup (long id) throws EntityNotFoundException {
        accessGuard.method (MiscUtils.getMethod ());
        
        GroupEntity period = CACHE.getOrPut (id, 
            () -> groupsRepository.findById (id).orElse (null)
        );
        
        if (period != null) { return period; }
        
        String message = "Unknown group credits `" + id + "`";
        throw new EntityNotFoundException (message);
    }
    
    @ProtectedMethod
    public List <Pair <String, String>> getGroupTypes () {
        accessGuard.method (MiscUtils.getMethod ());
        
        return Arrays.asList (GroupType.values ()).stream ()
             . map     (type -> Pair.mp (type.name (), type.getName ()))
             . collect (Collectors.toList ());
    }
    
    @ProtectedMethod
    public GroupEntity createGroup (String name, String description, 
            PeriodEntity period, GroupType type, WUser head) {
        accessGuard.method (MiscUtils.getMethod ());
        
        final GroupEntity entity = new GroupEntity ();
        entity.setDescription (description);
        entity.setHead (head.getEntity ());
        entity.setPeriod (period);
        entity.setName (name);
        entity.setType (type);
        
        return groupsRepository.save (entity);
    }
    
    @ProtectedMethod
    public GroupAssignmentEntity createGroupAssignment (WUser user, RoleEntity role, 
            GroupEntity group, GroupAssignmentStatus status, String comment, 
            WUser committer) {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (GroupAssignmentStatus.IN_GROUP.equals (status)) {
            Objects.requireNonNull (role, "User role in group must be specified");
            return addUserToGroup (user, role, group, comment, committer);
        } 
        
        if (GroupAssignmentStatus.EXCEPTED.equals (status)) {
            return removeUserFromGroup (user, group, comment, committer);
        }
        
        return null;
    }
    
    private GroupAssignmentEntity addUserToGroup (WUser user, RoleEntity role,
            GroupEntity group, String comment, WUser committer) {
        GroupAssignmentEntity assignment = gAssignmentsRepository
        . findByUserAndGroup (user.getEntity (), group);
        final PeriodEntity period = group.getPeriod ();
        
        if (assignment != null) {
            final RoleEntity currentRole = assignment.getRole ();
            usersService.removeRole (user, period, currentRole);
        } else {
            assignment = new GroupAssignmentEntity (user.getEntity (), 
                                          null, group, null, comment);
        }
        
        usersService.addRole (user, group.getPeriod (), role, committer);
        assignment.setStatus (GroupAssignmentStatus.IN_GROUP);
        assignment.setCommitter (committer.getEntity ());
        assignment.setIssued (LocalDateTime.now (clock));
        assignment.setComment (comment);
        assignment.setRole (role);
        
        return gAssignmentsRepository.save (assignment);
    }
    
    private GroupAssignmentEntity removeUserFromGroup (WUser user,
            GroupEntity group, String comment, WUser committer) {
        GroupAssignmentEntity assignment = gAssignmentsRepository
        . findByUserAndGroup (user.getEntity (), group);
        final PeriodEntity period = group.getPeriod ();
        
        if (assignment != null) {
            final RoleEntity currentRole = assignment.getRole ();
            usersService.removeRole (user, period, currentRole);
        } else {
            assignment = new GroupAssignmentEntity (user.getEntity (), 
                                          null, group, null, comment);
        }
        
        assignment.setStatus (GroupAssignmentStatus.EXCEPTED);
        assignment.setCommitter (committer.getEntity ());
        assignment.setIssued (LocalDateTime.now (clock));
        assignment.setComment (comment);
        
        return gAssignmentsRepository.save (assignment);
    }
    
    @ProtectedMethod
    public List <GroupMember> getGroupMembers (GroupEntity group) {
        accessGuard.method (MiscUtils.getMethod ());
        
        return gAssignmentsRepository.findByGroup (group).stream ()
             . filter  (row -> row.getStatus ().equals (GroupAssignmentStatus.IN_GROUP))
             . map     (row -> new GroupMember (row.getUser (), row.getRole ()))
             . collect (Collectors.toList ());
    }
    
    @ProtectedMethod
    public List <GroupEntity> getUserGroups (WUser user) {
        accessGuard.method (MiscUtils.getMethod ());
        
        return gAssignmentsRepository.findByUser (user.getEntity ()).stream ()
             . filter  (row -> row.getStatus ().equals (GroupAssignmentStatus.IN_GROUP))
             . map     (GroupAssignmentEntity::getGroup)
             . collect (Collectors.toList ());
    }
    
}
