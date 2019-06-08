package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import ru.shemplo.conduit.appserver.entities.AbsEntity;
import ru.shemplo.conduit.appserver.utils.LRUCache;

public class AbsCachedRepository <T extends AbsEntity> extends SimpleJpaRepository <T, Long> {

    protected final AbsEntityRepository <T> repository;
    protected final LRUCache <T> CACHE;
    
    public AbsCachedRepository (Class <T> domainClass, EntityManager em, 
            AbsEntityRepository <T> repository, int cacheSize) {
        super (domainClass, em);
        
        this.CACHE = new LRUCache<> (cacheSize);
        this.repository = repository;
    }
    
    @Override
    public Optional <T> findById (Long id) throws EntityNotFoundException {
        System.out.println ("AbsCachedRepository.findById(" + id + ")");
        
        T entity = CACHE.getOrPut (id, () -> {
            System.out.println ("Need to reload " + id);
            return repository.findById (id).get ();
        });
        if (entity != null) { return Optional.of (entity); }
        
        String message = String.format ("Entity (id=%d) does not exist", id);
        throw new EntityNotFoundException (message);
    }
    
}
