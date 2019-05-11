package ru.shemplo.conduit.appserver.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataCollector;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataType;
import ru.shemplo.conduit.appserver.entities.repositories.PersonalDataRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.LRUCache;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class PersonalDataService {
    
    private final PersonalDataRepository dataRepository;
    private final AccessGuard accessGuard;
    
    private static final int CACHE_SIZE = 64;
    
    private final LRUCache <PersonalDataCollector> CACHE = new LRUCache <> (CACHE_SIZE);
    
    @ProtectedMethod
    public PersonalDataCollector getPersonalData (WUser user, PeriodEntity period, PersonalDataType type) {
        accessGuard.method (MiscUtils.getMethod (), period);
        
        long id = PersonalDataCollector.hash (user.getId (), period.getId (), (long) type.ordinal ());
        
        PersonalDataCollector data = CACHE.getOrPut (id, () -> {
            final PersonalDataCollector collector = new PersonalDataCollector (user, period, type);
            dataRepository.findByUserAndPeriodAndType (user.getEntity (), period, type).stream ()
                          .map     (ent -> Pair.mp (ent.getField ().getKey (), ent.deserialize ()))
                          .forEach (pair -> collector.put (pair.getF (), pair.getS ()));
            return collector;
        });
        
        if (data != null) { return data; }
        
        String message = "Unknown personal data credits `" + user.getId () + "," 
                       + period.getId () + ", " + type + "`";
        throw new EntityNotFoundException (message);
    }
    
}
