package ru.shemplo.conduit.appserver.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataEntity;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataType;
import ru.shemplo.conduit.appserver.entities.repositories.PersonalDataRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.ExtendedLRUCache;
import ru.shemplo.snowball.stuctures.Trio;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class PersonalDataService {
    
    private final PersonalDataRepository dataRepository;
    private final AccessGuard accessGuard;
    
    private static final int CACHE_SIZE = 64;
    
    private final ExtendedLRUCache <Trio <Long, Long, PersonalDataType>, PersonalDataEntity> 
        CACHE = new ExtendedLRUCache <> (CACHE_SIZE, data -> 
            Trio.mt (data.getUser ().getId (), data.getPeriod ().getId (), 
                     data.getType ())
        );
    
    @ProtectedMethod
    public PersonalDataEntity getPersonalData (WUser user, 
            PeriodEntity period, PersonalDataType type) {
        accessGuard.method (MiscUtils.getMethod (), period);
        
        PersonalDataEntity data = CACHE.getOrPut (Trio.mt (user.getId (), period.getId (), type), 
            () -> dataRepository.findByUserAndPeriodAndType (user.getEntity (), period, type)
        );
        
        if (data != null) { return data; }
        
        String message = "Unknown personal data credits `" + user.getId () + "," 
                       + period.getId () + ", " + type + "`";
        throw new EntityNotFoundException (message);
    }
    
}
