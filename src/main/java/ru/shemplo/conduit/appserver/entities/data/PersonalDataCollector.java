package ru.shemplo.conduit.appserver.entities.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.shemplo.conduit.appserver.entities.Identifiable;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.snowball.stuctures.Trio;

@EqualsAndHashCode (callSuper = true, exclude = "values")
public class PersonalDataCollector extends Trio <Long, Long, PersonalDataType> implements Identifiable {
    
    public static Long hash (Long userID, Long periodID, Long dataTypeIndex) {
        return (userID << 36) + (periodID << 8) + dataTypeIndex;
    }
    
    public PersonalDataCollector (WUser user, PeriodEntity period, PersonalDataType T) {
        super (user.getId (), period.getId (), T);
    }

    @Override
    public Long getId () { return hash (F, S, (long) T.ordinal ()); }
    
    @Getter private final Map <String, Object> values = new HashMap <> ();
    
    public void put (String key, Object value) {
        values.put (key, value);
    }
    
    public <R> R get (String key) {
        @SuppressWarnings ("unchecked") R result = (R) values.get (key);
        return result;
    }
    
    public <R> Optional <R> geto (String key) {
        return Optional.ofNullable (get (key));
    }
    
}
