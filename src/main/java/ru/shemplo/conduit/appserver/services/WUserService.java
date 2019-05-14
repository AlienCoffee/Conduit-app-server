package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.RoleAssignmentEntity;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.entities.repositories.RoleAssignmentEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.UserEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.ExtendedLRUCache;
import ru.shemplo.conduit.appserver.utils.PhoneValidator;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class WUserService implements UserDetailsService {

    private final RoleAssignmentEntityRepository rolesARepository;
    private final PersonalDataService personalDataService;
    private final UserEntityRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessGuard accessGuard;
    @Getter private final Clock clock;
    
    private static final int CACHE_SIZE = 64;
    
    private final ExtendedLRUCache <String, WUser> 
        CACHE_BY_LOGIN = new ExtendedLRUCache <> (CACHE_SIZE, u -> u.getEntity ().getLogin ()),
        CACHE_BY_PHONE = new ExtendedLRUCache <> (CACHE_SIZE, u -> u.getEntity ().getPhone ());
    
    @Override @ProtectedMethod
    public UserDetails loadUserByUsername (String loginOrPhone) throws UsernameNotFoundException {
        WUser user = CACHE_BY_LOGIN.getOrPut (loginOrPhone, () -> {
            UserEntity tmp = usersRepository.findByLogin (loginOrPhone);
            return tmp == null ? null : new WUser (tmp, this);
        });
        if (user == null) {
            String formPhone = PhoneValidator.format (loginOrPhone);
            user = CACHE_BY_PHONE.getOrPut (formPhone, () -> {
                UserEntity tmp = usersRepository.findByPhone (formPhone);
                return tmp == null ? null : new WUser (tmp, this);
            });
        }
        
        if (user != null) { return user; }
        
        String message = "Unknown user credits `" + loginOrPhone + "`";
        throw new UsernameNotFoundException (message);
    }
    
    @ProtectedMethod
    public WUser getUser (long id) {
        WUser user = CACHE_BY_PHONE.getOrPut (id, () -> {
            UserEntity tmp = usersRepository.findById (id).orElse (null);
            return tmp == null ? null : new WUser (tmp, this);
        });
        
        if (user != null) { return user; }
        
        String message = "Unknown user credits `" + id + "`";
        throw new EntityNotFoundException (message);
    }
    
    @ProtectedMethod
    public Collection <UserEntity> getAllUsers () {
        accessGuard.method (MiscUtils.getMethod ());
        return usersRepository.findAll ();
    }
    
    public WUser createUser (String login, String phone, String password)
            throws EntityExistsException {
        try {
            // Check that login and phone is not used yet
            loadUserByUsername (login);    loadUserByUsername (phone);
            throw new EntityExistsException ("Login is already used");
        } catch (UsernameNotFoundException unfe) {
            final String encoded = passwordEncoder.encode (password);
            UserEntity entity = new UserEntity (login, phone, encoded, false);
            WUser user = new WUser (usersRepository.save (entity), this);
            CACHE_BY_PHONE.put (user); CACHE_BY_LOGIN.put (user);
            
            return user;
        }
    }
    
    public Map <PeriodEntity, List <RoleEntity>> getAllUserRoles (UserEntity user) {
        Map <PeriodEntity, List <RoleEntity>> result = new HashMap <> ();
        rolesARepository.findByUser (user).forEach (entry -> {
            result.putIfAbsent (entry.getPeriod (), new ArrayList <> ());
            result.get (entry.getPeriod ()).add (entry.getRole ());
        });
        
        return result;
    }
    
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
            CACHE_BY_PHONE.invalidate (user.getId ()); 
            CACHE_BY_LOGIN.invalidate (user.getId ());
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
            CACHE_BY_PHONE.invalidate (user.getId ()); 
            CACHE_BY_LOGIN.invalidate (user.getId ());
        } else { return; }
        
        rolesARepository.delete (assignment);
    }
    
}
