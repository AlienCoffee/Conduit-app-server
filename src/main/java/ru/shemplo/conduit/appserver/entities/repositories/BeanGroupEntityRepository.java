package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.snowball.utils.MiscUtils;

@Primary @Repository
public class BeanGroupEntityRepository extends AbsCachedRepository <GroupEntity> 
    implements GroupEntityRepository {
    
    public BeanGroupEntityRepository (EntityManager em, GroupEntityRepository repository) {
        super (GroupEntity.class, em, repository, 64);
    }

    @Override
    public List <Long> findAllIds () {
        GroupEntityRepository repo = MiscUtils.cast (repository);
        return repo.findAllIds ();
    }

    @Override
    public List <Long> findIdsByPeriod (PeriodEntity period) {
        GroupEntityRepository repo = MiscUtils.cast (repository);
        return repo.findIdsByPeriod (period);
    }

    @Override
    public List <GroupEntity> findByPeriod (PeriodEntity period) {
        GroupEntityRepository repo = MiscUtils.cast (repository);
        return repo.findByPeriod (period);
    }
    
}
