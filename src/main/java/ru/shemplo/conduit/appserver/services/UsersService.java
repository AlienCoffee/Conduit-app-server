package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.util.Collection;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.repositories.UserEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.PhoneValidator;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class UsersService extends AbsCachedService <WUser> implements UserDetailsService {
    
    private final UserEntityRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired private AccessGuard accessGuard;
    @Getter private final Clock clock;

    @Override
    protected WUser loadEntity (Long id) {
        UserEntity entity = usersRepository.findById (id).orElse (null);
        return entity != null ? new WUser (entity) : null;
    }
    
    @Override
    protected int getCacheSize () { return 128; }
    
    @Override
    public UserDetails loadUserByUsername (String loginOrPhone) 
            throws UsernameNotFoundException {
        UserEntity entityL = usersRepository.findByLogin (loginOrPhone);
        if (entityL != null) { return new WUser (entityL); }
        
        loginOrPhone = PhoneValidator.format (loginOrPhone);
        UserEntity entityP = usersRepository.findByPhone (loginOrPhone);
        if (entityP != null) { return new WUser (entityP); }
        
        throw new UsernameNotFoundException (loginOrPhone);
    }
    
    public WUser getUser_ss (Long id) {
        return getEntity (id);
    }
    
    @ProtectedMethod
    public WUser getUser (Long id) {
        accessGuard.method (MiscUtils.getMethod ());
        return getEntity (id);
    }
    
    @ProtectedMethod
    public Collection <UserEntity> getAllUsers  () {
        accessGuard.method (MiscUtils.getMethod ());
        return usersRepository.findAll ();
    }
    
    public WUser createUser (String login, String phone, String password)
            throws EntityExistsException {
        try {
            // Check that login and phone is not used yet
            loadUserByUsername (login);    loadUserByUsername (phone);
            throw new EntityExistsException ("Login or phone is already used");
        } catch (UsernameNotFoundException unfe) {
            final String encoded = passwordEncoder.encode (password);
            UserEntity entity = new UserEntity (login, phone, encoded, false);
            return new WUser (usersRepository.save (entity));
        }
    }
    
    /*
    @ProtectedMethod
    public void addRole (WUser user, PeriodEntity period, RoleEntity role, WUser committer) {
        accessGuard.method (MiscUtils.getMethod ());
        UserEntity userEntity = user.getEntity ();
        
        if (role.getTemplate () != null) {
            final PersonalDataTemplate template = role.getTemplate ();
            if (!personalDataService.isUserRegisteredForPeriodWithTemplate (user, period, template)) {
                throw new IllegalStateException ("User doesn't have required personal data");
            }
        }
        
        RoleAssignmentEntity assignment = new RoleAssignmentEntity (userEntity, period, role);
        if (rolesARepository.findByUserAndPeriodAndRole (userEntity, period, role) == null) {
            CACHE.invalidate (user.getId ()); 
        } else { return; } // user already have such assignment
        
        assignment.setIssued (LocalDateTime.now (clock));
        assignment.setCommitter (committer.getEntity ());
        rolesARepository.save (assignment);
    }
    
    @ProtectedMethod
    public void removeRole (WUser user, PeriodEntity period, RoleEntity role) {
        accessGuard.method (MiscUtils.getMethod ());
        UserEntity userEntity = user.getEntity ();
        
        final RoleAssignmentEntity assignment = rolesARepository
        . findByUserAndPeriodAndRole (userEntity, period, role);
        
        if (assignment != null) {
            CACHE.invalidate (user.getId ());
        } else { return; }
        
        rolesARepository.delete (assignment);
    }
    */
    
}
