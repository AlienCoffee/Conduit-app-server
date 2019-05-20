package ru.shemplo.conduit.appserver.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import ru.shemplo.conduit.appserver.entities.Identifiable;
import ru.shemplo.conduit.appserver.entities.Named;
import ru.shemplo.snowball.stuctures.Pair;

public class Utils {
    
    // with hope that platform won't have ID after 2^31
    public static final Long hash2 (Identifiable a, Identifiable b) {
        return hash2 (a.getId (), b.getId ());
    }
    
    public static final Long hash2 (Long a, Long b) {
        return ((a & 0xff_ff_ff_ffL) << 32) | (b & 0xff_ff_ff_ffL);
    }
    
    public static final Pair <Long, Long> dehash2 (Long id) {
        return Pair.mp ((id >>> 32) & 0xff_ff_ff_ffL, id & 0xff_ff_ff_ffL);
    }
    
    public static <T extends Named> String toString (String prefix, Collection <T> collection) {
        return collection.stream ().map (T::getName)
        . collect (Collectors.joining (", ", prefix + " [", "]"));
    }
    
}
