package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.GuardRuleEntity;

public interface GuardRuleEntityRepository extends AbsEntityRepository <GuardRuleEntity> {
    
    public GuardRuleEntity findByObject (String object);
    
}
