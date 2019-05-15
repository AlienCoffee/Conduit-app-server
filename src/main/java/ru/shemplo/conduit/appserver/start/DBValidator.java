package ru.shemplo.conduit.appserver.start;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.PeriodStatus;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.repositories.PeriodEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.UserEntityRepository;
import ru.shemplo.snowball.stuctures.Pair;

@Component
@RequiredArgsConstructor
public class DBValidator {
    
    @Transactional public void validate () {
        createAdminUserIfNotExists ();
        
        createSystemStudyPeriodIfNotExists ();
    }
    
    private final PeriodEntityRepository studyPeriodsRepository;
    private final ConfigurableEnvironment configurableEnvironment;
    private final UserEntityRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    
    private void createAdminUserIfNotExists () {
        final String prefix = "server.admin.";
        final Map <String, String> data 
            = Arrays.asList ("login", "phone", "password")
            . stream  ()
            . map     (s -> Pair.mp (s, s))
            . map     (p -> p.applyS (prefix::concat))
            . map     (p -> p.applyS (configurableEnvironment::getProperty))
            . collect (Collectors.toMap (Pair::getF, Pair::getS));
        
        if (data.get ("phone") != null) {
            UserEntity admin = usersRepository.findByPhone (data.get ("phone"));
            if (admin != null) { return; }            
        }
        
        String login    = Optional.ofNullable (data.get ("login")).orElse    ("admin");
        String phone    = Optional.ofNullable (data.get ("phone")).orElse    ("");
        String password = Optional.ofNullable (data.get ("password")).orElse ("admin");
        password = passwordEncoder.encode (password);
        
        UserEntity admin = new UserEntity (login, phone, password, true);
        admin = usersRepository.save (admin);
        UserEntity.setAdmin (admin);
    }
    
    private void createSystemStudyPeriodIfNotExists () {
        final String name = "$system";
        
        PeriodEntity period = studyPeriodsRepository
                                 . findByName (name);
        if (period == null) {
            final UserEntity admin = UserEntity.getAdminEntity ();
            final LocalDateTime from = LocalDateTime.now (clock);
            final PeriodStatus status = PeriodStatus.CREATED;
            
            period = new PeriodEntity (name, "", from, null, status, true);
            period.setCommitter (admin);
            period.setIssued (from);
            
            period = studyPeriodsRepository.save (period);
        }
        
        PeriodEntity.setSystem (period);
    }
    
}
