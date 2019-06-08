package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.snowball.utils.MiscUtils;

@Primary @Repository
public class BeanUserEntityRepository extends AbsCachedRepository <UserEntity> implements UserEntityRepository {

    public BeanUserEntityRepository (EntityManager em, UserEntityRepository repository) {
        super (UserEntity.class, em, repository, 128);
    }

    @Override
    public List <Long> findAllIds () {
        UserEntityRepository repo = MiscUtils.cast (repository);
        return repo.findAllIds ();
    }

    @Override
    public UserEntity findByPhone (String phone) {
        UserEntityRepository repo = MiscUtils.cast (repository);
        return repo.findByPhone (phone);
    }

    @Override
    public UserEntity findByLogin (String login) {
        UserEntityRepository repo = MiscUtils.cast (repository);
        return repo.findByLogin (login);
    }
    
}
