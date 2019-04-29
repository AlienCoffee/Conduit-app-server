package ru.shemplo.conduit.appserver.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.Identifiable;
import ru.shemplo.conduit.appserver.utils.LRUCache;

public class CacheTest {
    
    private static final Function <Integer, LRUCache <IdString>>
        FACTORY = size -> new LRUCache <> (size);
        
    @Getter
    @ToString
    @AllArgsConstructor
    private static class IdString implements Identifiable {
        
        private final Long id;
        
        private final String value;
        
    }
    
    @Test
    @DisplayName ("Basic insertion to cache")
    public void testPutValue () {
        IdString string = new IdString (34L, "Hello Test");
        LRUCache <IdString> cache = FACTORY.apply (32);
        
        assert cache.put (string) != null;
        
        String result = cache.get (string.getId ()).getValue ();
        assertEquals (string.getValue (), result);
    }
    
    @Test
    @DisplayName ("Check of cache size limit")
    public void testCacheLimit () {
        int size = 4;
        
        LRUCache <IdString> cache = FACTORY.apply (size);
        List <IdString> strings = new ArrayList <> ();
        for (int i = 0; i < size + 2; i++) {
            long id = size + i;
            
            IdString string = new IdString (id, "Value" + i);
            assertNotNull (cache.put (string));
            strings.add (string);
        }
        
        assert cache.get (strings.get (0).getId ()) == null;
    }
    
    @Test
    @DisplayName ("Check that node on top after get")
    public void testActualizationAfterGet () {
        int size = 4;
        
        LRUCache <IdString> cache = FACTORY.apply (size);
        List <IdString> strings = new ArrayList <> ();
        for (int i = 0; i < size; i++) {
            long id = size + i;
            
            IdString string = new IdString (id, "Value" + i);
            assertNotNull (cache.put (string));
            strings.add (string);
        }
        
        // cache is full
        assert cache.get (strings.get (0).getId ()) != null;
        
        for (int i = size; i < size * 2 - 1; i++) {
            long id = size + i;
            
            IdString string = new IdString (id, "Value" + i);
            assertNotNull (cache.put (string));
            strings.add (string);
        }
        
        // cache is updated except 1 node
        assert cache.get (strings.get (0).getId ()) != null;
    }
    
}
