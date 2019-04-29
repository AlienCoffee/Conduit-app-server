package ru.shemplo.conduit.appserver.entities.repositories;

import ru.shemplo.conduit.appserver.entities.StudyPeriodEntity;
import ru.shemplo.conduit.appserver.entities.TeacherPersonalityEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

public interface TeacherPersonalityEntityRepository extends AbsPersonalityEntityRepository <TeacherPersonalityEntity> {
    
    public TeacherPersonalityEntity findByUserAndPeriod (UserEntity user, StudyPeriodEntity period);
    
}
