package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import ru.shemplo.conduit.appserver.entities.AbsEntity;
import ru.shemplo.conduit.appserver.utils.LRUCache;

public abstract class AbsCachedRepository <T extends AbsEntity> extends SimpleJpaRepository <T, Long> {

    protected final AbsEntityRepository <T> repository;
    protected final EntityManager emanager;
    protected final LRUCache <T> CACHE;
    
    public AbsCachedRepository (Class <T> domainClass, EntityManager em, 
            AbsEntityRepository <T> repository, int cacheSize) {
        super (domainClass, em);
        
        this.CACHE = new LRUCache <> (cacheSize);
        this.repository = repository;
        this.emanager = em;
    }
    
    @Override
    public Optional <T> findById (Long id) throws EntityNotFoundException {
        return repository.findById (id);
        
        /* TODO: for future optimization
        System.out.println ("AbsCachedRepository.findById(" + id + ") ~ " + getDomainClass ());
        
        T entity = CACHE.getOrPut (id, () -> {
            Object obj = emanager.createQuery ("SELECT ent.id, ent.period.id FROM GroupEntity ent WHERE ent.id = 1").getSingleResult ();
            System.out.println (obj.getClass ());
            
            System.out.println ("Need to reload " + id);
            return repository.findById (id).get ();
        });
        if (entity != null) { return Optional.of (entity); }
        
        String message = String.format ("Entity (id=%d) does not exist", id);
        throw new EntityNotFoundException (message);
        */
    }
    
}
