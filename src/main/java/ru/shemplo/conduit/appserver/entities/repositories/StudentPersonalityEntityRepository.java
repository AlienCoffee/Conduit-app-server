package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.StudentPersonalityEntity;
import ru.shemplo.conduit.appserver.entities.StudyPeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

public interface StudentPersonalityEntityRepository extends AbsPersonalityEntityRepository <StudentPersonalityEntity> {
    
    public StudentPersonalityEntity findByUserAndPeriod (UserEntity user, StudyPeriodEntity period);
    
}
