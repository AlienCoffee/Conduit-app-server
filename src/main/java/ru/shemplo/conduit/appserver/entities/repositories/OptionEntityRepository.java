package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.OptionEntity;

public interface OptionEntityRepository extends AbsEntityRepository <OptionEntity> {
    
    public OptionEntity findByName (String name);
    
}
