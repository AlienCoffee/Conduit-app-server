package ru.shemplo.conduit.appserver.services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
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
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.conduit.appserver.web.form.WebFormField;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class PersonalDataService {
    
    private final RegisteredPeriodRoleEntityRepository registeredRoleRepository;
    private final PersonalDataRepository dataRepository;
    @Autowired private AccessGuard accessGuard;
    
    private static final int CACHE_SIZE = 64;
    
    private final LRUCache <PersonalDataCollector> CACHE = new LRUCache <> (CACHE_SIZE);
    
    @ProtectedMethod
    public PersonalDataCollector getPersonalData (WUser user, PeriodEntity period) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        long id = PersonalDataCollector.hash (user.getId (), period.getId ());
        
        PersonalDataCollector data = CACHE.getOrPut (id, () -> {
            final PersonalDataCollector collector = new PersonalDataCollector (user, period);
            dataRepository.findByUserAndPeriod (user.getEntity (), period).stream ()
                          .map (ent -> Pair.mp (ent.getField ().getName (), ent.deserialize ()))
                          .forEach (pair -> collector.put (pair.getF (), pair.getS ()));
            return collector;
        });
        
        if (data != null) { return data; }
        
        String message = "Unknown personal data credits `" + user.getId () 
                       + "," + period.getId () + "`";
        throw new EntityNotFoundException (message);
    }
    
    @ProtectedMethod
    @Transactional public void savePersonalData (WUser user, PeriodEntity period, 
            PersonalDataTemplate template, Map <String, String> data) {
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        final Set <String> present = new HashSet <> ();
        List <PersonalDataEntity> rows = template.getRows ().stream ()
           . filter  (row -> row instanceof WebFormField)
           . map     (row -> {
               @SuppressWarnings ("unchecked") WebFormField <PersonalDataField> 
                   field = (WebFormField <PersonalDataField>) row;
               return field;
           })
           . filter  (row -> data.containsKey (row.getParameterName ()))
           . peek    (row -> {
               if (row.isRequired ()) { present.add (row.getParameterName ()); }
           })
           . map     (row -> new PersonalDataEntity (user.getEntity (), period, row.getField (), null))
           . map     (field -> dataRepository.findOne (Example.of (field)).orElse (field))
           . peek    (field -> field.setValue (data.get (field.getField ().getName ())))
           . peek    (field -> field.deserialize ()) // check that data is correct
           . collect (Collectors.toList ());
        
        if (present.size () < template.getNumberOfRequired ()) {
            String message = "Not enough required arguments";
            throw new IllegalStateException (message);
        }
        
        rows.forEach (dataRepository::save);
        
        RegisteredPeriodRoleEntity role = new RegisteredPeriodRoleEntity (
            user.getEntity (), period, template
        );
        
        if (!registeredRoleRepository.exists (Example.of (role))) {
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
            user.getEntity (), period, template
        );
        
        return registeredRoleRepository.exists (Example.of (role));
    }
    
}
