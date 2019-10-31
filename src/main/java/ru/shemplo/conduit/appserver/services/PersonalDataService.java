package ru.shemplo.conduit.appserver.services;

import static ru.shemplo.conduit.appserver.entities.AssignmentStatus.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.data.*;
import ru.shemplo.conduit.appserver.entities.repositories.PersonalDataRepository;
import ru.shemplo.conduit.appserver.entities.repositories.RegisteredPeriodRoleEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.Utils;
import ru.shemplo.conduit.appserver.web.form.WebFormValue;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class PersonalDataService extends AbsCachedService <PersonalDataCollector> {
    
    private final RegisteredPeriodRoleEntityRepository registeredRoleRepository;
    private final PersonalDataRepository dataRepository;
    private final PeriodsService periodsService;
    @Autowired private AccessGuard accessGuard;
    private final UsersService usersService;
    private final Clock clock;
    
    @Override
    protected PersonalDataCollector loadEntity (Long id) {
        Pair <Long, Long> periodNuserIds = Utils.dehash2 (id);
        final PeriodEntity period = periodsService.getPeriod (periodNuserIds.S);
        final WUser user = usersService.getUser (periodNuserIds.F);
        
        final PersonalDataCollector collector = new PersonalDataCollector (user, period);
        dataRepository.findByUserAndPeriod (user.getEntity (), period).stream ()
        . map (ent -> Pair.mp (ent.getField ().getName (), ent.deserialize ()))
        . forEach (pair -> collector.put (pair.getF (), pair.getS ()));
        
        return collector;
    }

    @Override
    protected int getCacheSize () { return 64; }
    
    @ProtectedMethod
    public PersonalDataCollector getPersonalData (WUser user, PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        return getEntity (Utils.hash2 (user, period));
    }
    
    @Transactional 
    @ProtectedMethod 
    public void savePersonalData (WUser user, PeriodEntity period, PersonalDataTemplate template, 
            Map <String, String> data, WUser committer) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        final List <String> missed = new ArrayList <> ();
        List <PersonalDataEntity> rows = template.getRows ().stream ()
           . filter  (row -> row instanceof WebFormValue)
           . map     (row -> {
               WebFormValue <PersonalDataField> field = MiscUtils.cast (row);
               return field;
           })
           . filter  (row -> {
               var exist = data.containsKey (row.getParameterName ());
               if (!exist && row.isRequired ()) {
                   missed.add (row.getParameterName ());
               }
               return exist;
           })
           . map     (row -> new PersonalDataEntity (user.getEntity (), period, row.getParameter (), null))
           . map     (field -> dataRepository.findOne (Example.of (field)).orElse (field))
           . peek    (field -> field.setValue (data.get (field.getField ().getName ())))
           . peek    (field -> field.deserialize ()) // check that data is correct
           . collect (Collectors.toList ());
        
        if (missed.size () > 0) {
            final String message = "Not enough required arguments: " 
                + missed.stream ().collect (Collectors.joining (", "));
            throw new IllegalStateException (message);
        }
        
        rows.forEach (dataRepository::save);
        CACHE.invalidate (Utils.hash2 (user, period));
        
        if (!isUserRegisteredForPeriodWithTemplate (user, period, template)) {
            RegisteredPeriodRoleEntity role = new RegisteredPeriodRoleEntity (
                user.getEntity (), period, template, APPLICATION
            );
            
            role.setCommitter (committer.getEntity ());
            role.setAuthor (committer.getEntity ());
            
            LocalDateTime now = LocalDateTime.now (clock);
            role.setChanged (now);
            role.setIssued (now);
                
            registeredRoleRepository.save (role);
        }
    }
    
    @ProtectedMethod
    public boolean isUserRegisteredForPeriod (WUser user, PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        return isUserRegisteredForPeriodWithTemplate (user, period, null);
    }
    
    @ProtectedMethod
    public boolean isUserRegisteredForPeriodWithTemplate (WUser user, 
            PeriodEntity period, PersonalDataTemplate template) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        RegisteredPeriodRoleEntity role = new RegisteredPeriodRoleEntity (
            user.getEntity (), period, template, null
        );
        
        return registeredRoleRepository.exists (Example.of (role));
    }
    
    @ProtectedMethod
    public List <PersonalDataTemplate> getUserRegisteredTemplates (WUser user, PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        return registeredRoleRepository.findTempltesByPeriodAndUser (period, user.getEntity ());
    }
    
    @ProtectedMethod
    public List <RegisteredPeriodRoleEntity> getPendingRegistrations (WUser user, PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        // TODO: add filtering
        return registeredRoleRepository.findByPeriod (period);
    }
    
}
