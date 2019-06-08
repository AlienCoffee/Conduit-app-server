package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import ru.shemplo.conduit.appserver.entities.OptionEntity;
import ru.shemplo.snowball.utils.MiscUtils;

@Primary @Repository
public class BeanOptionEntityRepository extends AbsCachedRepository <OptionEntity> implements OptionEntityRepository {

    public BeanOptionEntityRepository (EntityManager em, OptionEntityRepository repository) {
        super (OptionEntity.class, em, repository, 64);
    }

    @Override
    public List <Long> findAllIds () {
        UserEntityRepository repo = MiscUtils.cast (repository);
        return repo.findAllIds ();
    }

    @Override
    public OptionEntity findByName (String name) {
        OptionEntityRepository repo = MiscUtils.cast (repository);
        return repo.findByName (name);
    }
    
}
