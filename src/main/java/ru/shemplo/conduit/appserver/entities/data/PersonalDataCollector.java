package ru.shemplo.conduit.appserver.entities.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.shemplo.conduit.appserver.entities.Identifiable;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.snowball.stuctures.Pair;

@EqualsAndHashCode (callSuper = true, exclude = "values")
public class PersonalDataCollector extends Pair <Long, Long> implements Identifiable {
    
    private static final long serialVersionUID = -6305896996021039983L;

    public static Long hash (Long userID, Long periodID) {
        return (userID << 32) + periodID;
    }
    
    public PersonalDataCollector (WUser user, PeriodEntity period) {
        super (user.getId (), period.getId ());
    }

    @Override
    public Long getId () { return hash (F, S); }
    
    @Getter private final Map <String, Object> values = new HashMap <> ();
    
    @JsonIgnore
    public void put (String key, Object value) {
        values.put (key, value);
    }
    
    @JsonIgnore
    public <R> R get (String key) {
        @SuppressWarnings ("unchecked") R result = (R) values.get (key);
        return result;
    }
    
    @JsonIgnore
    public <R> Optional <R> geto (String key) {
        return Optional.ofNullable (get (key));
    }
    
}
