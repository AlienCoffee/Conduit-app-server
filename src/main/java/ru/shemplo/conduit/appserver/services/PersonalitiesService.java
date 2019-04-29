package ru.shemplo.conduit.appserver.services;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.*;
import ru.shemplo.conduit.appserver.entities.repositories.StudentPersonalityEntityRepository;
import ru.shemplo.conduit.appserver.entities.repositories.TeacherPersonalityEntityRepository;

@Service
@RequiredArgsConstructor
public class PersonalitiesService {
    
    private final TeacherPersonalityEntityRepository teachersRepository;
    private final StudentPersonalityEntityRepository studentsRepository;
    
    public AbsPersonalityEntity getPersonality (UserEntity user, StudyPeriodEntity period) 
            throws EntityNotFoundException {
        try { return getStudentPersonality (user, period); } catch (Exception e) {}
        return getTeacherPersonality (user, period);
    }
    
    public TeacherPersonalityEntity getTeacherPersonality (UserEntity user, StudyPeriodEntity period) {
        AbsPersonalityEntity entity = teachersRepository.findByUserAndPeriod (user, period);
        return (TeacherPersonalityEntity) Optional.ofNullable (entity)
             . orElseThrow (() -> new EntityNotFoundException (NO_ENTITY_MESSAGE + ": teacher"));
    }
    
    public StudentPersonalityEntity getStudentPersonality (UserEntity user, StudyPeriodEntity period) {
        AbsPersonalityEntity entity = studentsRepository.findByUserAndPeriod (user, period);
        return (StudentPersonalityEntity) Optional.ofNullable (entity)
             . orElseThrow (() -> new EntityNotFoundException (NO_ENTITY_MESSAGE + ": student"));
    }
    
}
