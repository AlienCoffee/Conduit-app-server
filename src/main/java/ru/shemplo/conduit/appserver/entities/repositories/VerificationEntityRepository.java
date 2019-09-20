package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.VerificationEntity;

public interface VerificationEntityRepository extends AbsEntityRepository <VerificationEntity> {
    
    public VerificationEntity findByLogin (String login);
    
    public VerificationEntity findByPhone (String phone);
    
}
