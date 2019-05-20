package ru.shemplo.conduit.appserver.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import ru.shemplo.conduit.appserver.entities.Identifiable;
import ru.shemplo.conduit.appserver.utils.LRUCache;

public abstract class AbsCachedService <T extends Identifiable> {
    
    protected final LRUCache <T> CACHE = new LRUCache <> (getCacheSize ());
    
    protected abstract T loadEntity (Long id);
    
    protected abstract int getCacheSize ();
    
    /**
     * (Method protected for security purposes: in service must be 
     *  protected method, that will work with this one)<br />
     * 
     * @param id
     * 
     * @return
     * 
     */
    protected T getEntity (Long id) {
        T entity = CACHE.getOrPut (id, () -> loadEntity (id));
        if (entity != null) { return entity; }
        
        String message = String.format ("Entity (id=%d) does not exist", id);
        throw new EntityNotFoundException (message);
    }
    
    /**
     * 
     * @param ids
     * @param strict if entity doesn't exist than throws exception
     * 
     * @return
     * 
     */
    protected List <T> getEntities (Iterable <Long> ids, boolean strict) {
        List <T> entities = new ArrayList <> ();
        for (Long id : ids) {
            try   { entities.add (getEntity (id)); } 
            catch (EntityNotFoundException enfe) {
                if (strict) { throw enfe; }
            }
        }
        
        return entities;
    }
    
}
