package ru.shemplo.conduit.appserver.services;

import java.util.Collection;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.RoleAssignmentEntity;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.StudyPeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.repositories.RoleAssignmentEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.UserEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.utils.ExtendedLRUCache;
import ru.shemplo.snowball.stuctures.Pair;

@Service
@RequiredArgsConstructor
public class WUserService implements UserDetailsService {

    private final RoleAssignmentEntityRepository roleAsmtsRepository;
    private final UserEntityRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final int CACHE_SIZE = 64;
    
    private final ExtendedLRUCache <String, WUser> 
        CACHE_BY_LOGIN = new ExtendedLRUCache <> (CACHE_SIZE, u -> u.getEntity ().getLogin ()),
        CACHE_BY_PHONE = new ExtendedLRUCache <> (CACHE_SIZE, u -> u.getEntity ().getPhone ());
    
    @Override
    public UserDetails loadUserByUsername (String loginOrPhone) throws UsernameNotFoundException {
        WUser user = CACHE_BY_LOGIN.getOrPut (loginOrPhone, () -> {
            UserEntity tmp = usersRepository.findByLogin (loginOrPhone);
            return tmp == null ? null : new WUser (tmp, this);
        });
        if (user == null || StringUtils.isEmpty (user.getPassword ())) {
            user = CACHE_BY_PHONE.getOrPut (loginOrPhone, () -> {
                UserEntity tmp = usersRepository.findByPhone (loginOrPhone);
                return tmp == null ? null : new WUser (tmp, this);
            });
        }
        
        if (user != null && !StringUtils.isEmpty (user.getPassword ())) {
            return user;
        }
        
        throw new UsernameNotFoundException ("Unknown name");
    }
    
    public WUser getUser (long id) {
        WUser user = CACHE_BY_PHONE.getOrPut (id, () -> {
            UserEntity tmp = usersRepository.findById (id).get ();
            return tmp == null ? null : new WUser (tmp, this);
        });
        
        if (user != null && !StringUtils.isEmpty (user.getPassword ())) {
            return user;
        }
        
        throw new EntityNotFoundException ();
    }
    
    public WUser createUser (String login, String phone, String password) {
        final String encoded = passwordEncoder.encode (password);
        UserEntity entity = new UserEntity (login, phone, encoded, false);
        return new WUser (usersRepository.save (entity), this);
    }
    
    public Collection <Pair <StudyPeriodEntity, RoleEntity>> getAllUsersRoles (UserEntity user) {
        return null;
    }
    
    public Collection <RoleEntity> getUsersRolesInStudyPeriod (
            UserEntity user, StudyPeriodEntity period) {
        return null;
    }
    
    @Transactional
    public WUser chandeUserRole (UserEntity user, RoleEntity role, 
                          StudyPeriodEntity period, boolean add) {
        if (user.getId () == null) {
            throw new IllegalArgumentException ("Given user is not saved");
        }
        
        RoleAssignmentEntity entity = new RoleAssignmentEntity (user, period, role);
        
        if (add && roleAsmtsRepository.getByAll (user, period, role) == null) {
            roleAsmtsRepository.save (entity);
        } else if (!add) {
            roleAsmtsRepository.delete (entity);
        }
        
        /*
        if (add) { // adding new role to user
            user.getRoles ().add (roleEntity);
        } else { // remove role from user
            user.getRoles ().remove (roleEntity);
        }
        */
        
        return new WUser (usersRepository.save (user), this);
    }
    
}
