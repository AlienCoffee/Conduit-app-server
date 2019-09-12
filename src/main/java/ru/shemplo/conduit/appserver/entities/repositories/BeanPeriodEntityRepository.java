package ru.shemplo.conduit.appserver.entities.repositories;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.snowball.utils.MiscUtils;

//@Primary @Repository
public class BeanPeriodEntityRepository extends AbsCachedRepository <PeriodEntity> implements PeriodEntityRepository {
    
    public BeanPeriodEntityRepository (EntityManager em, PeriodEntityRepository repository) {
        super (PeriodEntity.class, em, repository, 32);
    }

    @Override
    public List <Long> findAllIds () {
        PeriodEntityRepository repo = MiscUtils.cast (repository);
        return repo.findAllIds ();
    }

    @Override
    public PeriodEntity findByName (String name) {
        PeriodEntityRepository repo = MiscUtils.cast (repository);
        return repo.findByName (name);
    }

    @Override
    public List <Long> findAllIdsOfAvailablePeriod (LocalDateTime moment) {
        return findAllIds ();
    }
    
}
