package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.AssignmentStatus;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.PeriodStatus;
import ru.shemplo.conduit.appserver.entities.groups.GroupAssignmentEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupJoinType;
import ru.shemplo.conduit.appserver.entities.groups.GroupType;
import ru.shemplo.conduit.appserver.entities.repositories.GroupAssignmentEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.GroupEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.web.dto.GroupMember;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupsService extends AbsCachedService <GroupEntity> {
    
    private final GroupAssignmentEntityRepository gAssignmentsRepository;
    private final GroupEntityRepository groupsRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected GroupEntity loadEntity (Long id) {
        return groupsRepository.findById (id).orElse (null);
    }
    
    @Override
    protected int getCacheSize () { return 64; }
    
    @ProtectedMethod
    public GroupEntity getGroup (Long id) throws EntityNotFoundException {
        final GroupEntity entity = getEntity (id);
        //final GroupEntity entity = groupsRepository.findById (id).get ();
        
        accessGuard.method (MiscUtils.getMethod (), entity.getPeriod ());
        return entity;
    }
    
    @ProtectedMethod
    public List <GroupEntity> getPeriodGroups (PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod (), period);
        
        List <Long> ids = groupsRepository.findIdsByPeriod (period);
        return getEntities (ids, true);
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
        accessGuard.method (MiscUtils.getMethod (), period);
        
        if (PeriodStatus.FINISHED.equals (period.getStatus ())) {
            String message = "Group can't be created in finished period";
            throw new IllegalStateException (message);
        }
        
        final GroupEntity entity = new GroupEntity ();
        entity.setDescription (description);
        entity.setHead (head.getEntity ());
        entity.setPeriod (period);
        entity.setName (name);
        entity.setType (type);
        
        log.info (entity.toTemplateString ());
        return groupsRepository.save (entity);
    }
    
    @ProtectedMethod
    public void createGroupJoin (WUser user, GroupEntity group) {
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod (), user);
        if (gAssignmentsRepository.findByUserAndGroup (user.getEntity (), group) != null) {
            throw new IllegalStateException ("User already joined the group");
        }
        
        if (GroupJoinType.ASSIGNMENT.equals (group.getJoinType ())) {
            String message = "Join to this group only by special assignment";
            throw new IllegalStateException (message);
        }
        
        GroupAssignmentEntity assignment = new GroupAssignmentEntity (
            user.getEntity (), group, AssignmentStatus.APPLICATION
        );
        
        if (GroupJoinType.FREE.equals (group.getJoinType ())) {
            assignment.setStatus (AssignmentStatus.ASSIGNED);
        }
        
        assignment.setIssued (LocalDateTime.now (clock));
        assignment.setCommitter (user.getEntity ());
        log.info (assignment.toTemplateString ());
        gAssignmentsRepository.save (assignment);
    }
    
    /*
    @ProtectedMethod
    public GroupAssignmentEntity createGroupAssignment (WUser user, RoleEntity role, 
            GroupEntity group, GroupAssignmentStatus status, String comment, 
            WUser committer) {
        Objects.requireNonNull (status, "Assignment status had to be defined");
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod ());
        
        if (GroupAssignmentStatus.IN_GROUP.equals (status)) {
            Objects.requireNonNull (role, "User role in group must be specified");
            return addUserToGroup (user, role, group, comment, committer);
        } 
        
        if (GroupAssignmentStatus.EXCEPTED.equals (status)) {
            return removeUserFromGroup (user, group, comment, committer);
        }
        
        if (GroupAssignmentStatus.APPLICATION.equals (status)) {
            Objects.requireNonNull (role, "User role in group must be specified");
            return createAppForAssignment (user, role, group, comment, committer);
        }
        
        final String message = "Unsupported assignment status";
        throw new IllegalArgumentException (message);
    }
    
    private GroupAssignmentEntity addUserToGroup (WUser user, RoleEntity role,
            GroupEntity group, String comment, WUser committer) {
        GroupAssignmentEntity assignment = gAssignmentsRepository
        . findByUserAndGroup (user.getEntity (), group);
        final PeriodEntity period = group.getPeriod ();
        
        if (assignment != null) {
            final RoleEntity currentRole = assignment.getRole ();
            rolesService.changeUserRoleInPeriod (period, user, currentRole, 
                                           EntityAction.REMOVE, committer);
        } else {
            assignment = new GroupAssignmentEntity (user.getEntity (), 
                                          null, group, null, comment);
        }
        
        rolesService.changeUserRoleInPeriod (period, user, role, 
                                   EntityAction.ADD, committer);
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
            rolesService.changeUserRoleInPeriod (period, user, currentRole, 
                                           EntityAction.REMOVE, committer);
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
    
    private GroupAssignmentEntity createAppForAssignment (WUser user, RoleEntity role,
            GroupEntity group, String comment, WUser committer) {
        GroupAssignmentEntity assignment = gAssignmentsRepository
        . findByUserAndGroup (user.getEntity (), group);
        
        if (assignment != null) {
            if (GroupAssignmentStatus.IN_GROUP.equals (assignment.getStatus ())) {
                final String message = "User is already in the particular group";
                throw new IllegalStateException (message);
            }
        } else {
            assignment = new GroupAssignmentEntity (user.getEntity (), 
                                          null, group, null, comment);
        }
        
        assignment.setStatus (GroupAssignmentStatus.APPLICATION);
        assignment.setCommitter (committer.getEntity ());
        assignment.setIssued (LocalDateTime.now (clock));
        assignment.setComment (comment);
        
        return gAssignmentsRepository.save (assignment);
    }
    */
    
    @ProtectedMethod
    public List <GroupMember> getGroupMembers (GroupEntity group) {
        accessGuard.method (MiscUtils.getMethod (), group.getPeriod ());
        accessGuard.group  (group);
        
        // TODO: user role in group // must be taken from period
        return gAssignmentsRepository.findByGroup (group).stream ()
             . filter  (row -> row.getStatus ().equals (AssignmentStatus.ASSIGNED))
             . map     (row -> new GroupMember (row.getUser (), null))
             . collect (Collectors.toList ());
    }
    
}
