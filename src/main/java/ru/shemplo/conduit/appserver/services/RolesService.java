package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.*;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.entities.data.RegisteredPeriodRoleEntity;
import ru.shemplo.conduit.appserver.entities.repositories.RegisteredPeriodRoleEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.RoleAssignmentEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.RoleEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.web.form.WebFormRow;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class RolesService extends AbsCachedService <RoleEntity> {
    
    private final RegisteredPeriodRoleEntityRepository registeredRoleRepository;
    private final RoleAssignmentEntityRepository assignmentsRepository;
    private final PersonalDataService personalDataService;
    private final RoleEntityRepository rolesRepository;
    @Autowired private AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected RoleEntity loadEntity (Long id) {
        return rolesRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    // unprotected // only for SecurityService //
    public List <Long> getUserRolesIds_ss (PeriodEntity period, UserEntity user) {
        return assignmentsRepository.findRolesIdsByPeriodAndUser (period, user);
    }
    
    // unprotected // only for SecurityService //
    public List <RoleEntity> getRoles_ss (Iterable <Long> ids) {
        return getEntities (ids, false);
    }
    
    @ProtectedMethod
    public RoleEntity getRole (Long id) {
        accessGuard.method (MiscUtils.getMethod ());
        return getEntity (id);
    }
    
    @ProtectedMethod
    public RoleEntity createRole (String name, PersonalDataTemplate template) {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (rolesRepository.findByName (name) != null) {
            throw new EntityExistsException ("Role exists");
        }
        
        RoleEntity entity = new RoleEntity (name, new HashSet <> (), template);
        return rolesRepository.save (entity);
    }
    
    @ProtectedMethod
    public Collection <RoleEntity> getAllRoles () {
        accessGuard.method (MiscUtils.getMethod ());
        return rolesRepository.findAll ();
    }
    
    @ProtectedMethod
    public RoleEntity addOptionToRole (RoleEntity role, OptionEntity option) {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (role.getOptions ().add (option)) {            
            CACHE.invalidate (role.getId ());
            accessGuard.invalidateAll ();
        }
                
        return rolesRepository.save (role);
    }
    
    @ProtectedMethod
    public RoleEntity removeOptionFromRole (RoleEntity role, OptionEntity option) {
        accessGuard.method (MiscUtils.getMethod ());
        
        if (role.getOptions ().remove (option)) {            
            CACHE.invalidate (role.getId ());
            accessGuard.invalidateAll ();
        }
         
        return rolesRepository.save (role);
    }
    
    @ProtectedMethod
    public Map <String, List <WebFormRow>> getPeriodRegisterTemplates () {
        accessGuard.method (MiscUtils.getMethod ());
        
        return Arrays.asList (PersonalDataTemplate.values ()).stream ()
             . map (Pair::dup)
             . map (pair -> pair.applyF (PersonalDataTemplate::getName))
             . map (pair -> pair.applyS (PersonalDataTemplate::getRows))
             . collect (Collectors.toMap (Pair::getF, Pair::getS));
    }
    
    @ProtectedMethod
    public Map <String, List <UserEntity>> getPeriodRegisteredUsers (PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod ());
        
        return Arrays.asList (PersonalDataTemplate.values ()).stream ()
             . map (Pair::dup)
             . map (pair -> pair.applyF (PersonalDataTemplate::getName))
             . map (pair -> pair.applyS (temp -> registeredRoleRepository
                          . findByPeriodAndTemplate (period, temp)))
             . map (pair -> pair.applyS (list -> list.stream ()
                          . map (entity -> entity.getUser ())
                          . collect (Collectors.toList ())))
             . collect (Collectors.toMap (Pair::getF, Pair::getS));
    }
    
    @ProtectedMethod
    public void changeUserRoleInPeriod (PeriodEntity period, WUser user, 
            RoleEntity role, EntityAction action, String comment, 
            WUser committer) {
        accessGuard.method (MiscUtils.getMethod ());
        
        switch (action) {
            case ADD:    addRole    (period, user, role, committer); break;
            case REMOVE: removeRole (period, user, role, committer); break;
            default:
                throw new UnsupportedOperationException ();
        }
        
        accessGuard.invalidateForUserInPeriod (user.getEntity (), period);
    }
    
    private void addRole (PeriodEntity period, WUser user, RoleEntity role, WUser committer) {
        if (role.getTemplate () != null) {
            final PersonalDataTemplate template = role.getTemplate ();
            if (!personalDataService.isUserRegisteredForPeriodWithTemplate (user, period, template)) {
                throw new IllegalStateException ("User doesn't have required personal data");
            }
        }
        
        final RoleAssignmentEntity assignment = new RoleAssignmentEntity (user.getEntity (), period, role);
        if (assignmentsRepository.findByUserAndPeriodAndRole (user.getEntity (), period, role) != null) {
            return; // user already have such assignment
        }
        
        assignment.setCommitter (committer.getEntity ());
        assignment.setIssued (LocalDateTime.now (clock));
        assignmentsRepository.save (assignment);
        
        if (role.getTemplate () != null) {
            final PersonalDataTemplate template = role.getTemplate ();
            final RegisteredPeriodRoleEntity roleAssignment = registeredRoleRepository
                . findByUserAndPeriodAndTemplate (user.getEntity (), period, template);
            roleAssignment.setStatus (AssignmentStatus.ASSIGNED);
            roleAssignment.setCommitter (committer.getEntity ());
            roleAssignment.setIssued (LocalDateTime.now (clock));
            registeredRoleRepository.save (roleAssignment);
        }
    }
    
    private void removeRole (PeriodEntity period, WUser user, RoleEntity role, WUser committer) {
        final RoleAssignmentEntity assignment = assignmentsRepository
        . findByUserAndPeriodAndRole (user.getEntity (), period, role);
        
        if (assignment == null) { return; } // user don't have role
        assignmentsRepository.delete (assignment);
        
        if (role.getTemplate () != null) {
            final PersonalDataTemplate template = role.getTemplate ();
            final RegisteredPeriodRoleEntity roleAssignment = registeredRoleRepository
                . findByUserAndPeriodAndTemplate (user.getEntity (), period, template);
            roleAssignment.setStatus (AssignmentStatus.REJECTED);
            roleAssignment.setCommitter (committer.getEntity ());
            roleAssignment.setIssued (LocalDateTime.now (clock));
            registeredRoleRepository.save (roleAssignment);
        }
    }
    
    @ProtectedMethod
    public void getAssignedUserRoleForPeriod (WUser user, PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        
    }
    
}
