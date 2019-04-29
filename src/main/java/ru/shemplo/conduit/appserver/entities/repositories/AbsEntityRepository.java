package ru.shemplo.conduit.appserver.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.shemplo.conduit.appserver.entities.AbsEntity;

public interface AbsEntityRepository <T extends AbsEntity> extends JpaRepository <T, Long> {
    
}
