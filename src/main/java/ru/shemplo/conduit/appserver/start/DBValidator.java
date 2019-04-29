package ru.shemplo.conduit.appserver.start;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.repositories.RoleEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.UserEntityRepository;
import ru.shemplo.snowball.stuctures.Pair;

@Component
@RequiredArgsConstructor
public class DBValidator {
    
    @Transactional public void validate () {
        createAdminRoleIfNotExists ();
        createAdminUserIfNotExists ();
    }
    
    private final ConfigurableEnvironment configurableEnvironment;
    private final RoleEntityRepository rolesRepository;
    private final UserEntityRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    
    private void createAdminRoleIfNotExists () {
        final String adminRole = RoleEntity.HEAD_ROLE;
        
        RoleEntity admin = rolesRepository.findByName (adminRole);
        if (admin != null) { return; }
        
        admin = new RoleEntity (adminRole, new HashSet <> (), null);
        rolesRepository.save (admin);
    }
    
    private void createAdminUserIfNotExists () {
        final String prefix = "server.admin.";
        final Map <String, String> data 
            = Arrays.asList ("login", "phone", "password")
            . stream  ()
            . map     (s -> Pair.mp (s, s))
            . map     (p -> p.applyS (prefix::concat))
            . map     (p -> p.applyS (configurableEnvironment::getProperty))
            . collect (Collectors.toMap (Pair::getF, Pair::getS));
        
        UserEntity admin = usersRepository.findByPhone (data.get ("phone"));
        if (admin != null) { return; }
        
        String password = passwordEncoder.encode (data.get ("password"));
        admin = new UserEntity (data.get ("login"), data.get ("phone"),
                                password, true);
        usersRepository.save (admin);
    }
    
}
