package ru.shemplo.conduit.appserver.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import ru.shemplo.conduit.appserver.entities.Identifiable;

public class ExtendedLRUCache <K, T extends Identifiable> extends LRUCache <T> {

    private final Map <K, T> map = new HashMap <> ();
    private final Function <T, K> keyProducer;
    
    public ExtendedLRUCache (int capacity, Function <T, K> key) {
        super (capacity);
        
        this.keyProducer = key;
    }
    
    @Override
    public synchronized T put (T value) {
        if (value == null) { return null; }
        
        K key = keyProducer.apply (value);
        if (key == null) { return null; }
        
        map.put (key, value);
        return super.put (value);
    }
    
    public synchronized T get (final K key) {
        final T value = map.get (key);
        if (value == null) {
            return null;
        }
        
        return get (value.getId ());
    }
    
    public synchronized T getOrPut (K key, Supplier <T> source) {
        T value = map.get (key);
        if (value == null) {
            value = put (source.get ());
        }
        
        return value;
    }
    
    @Override
    protected void onNodeRemoved (LRUCache <T>.Node node) {
        map.remove (keyProducer.apply (node.getValue ()));
    }
    
}
