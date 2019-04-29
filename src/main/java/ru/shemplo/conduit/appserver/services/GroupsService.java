package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.GroupEntity;
import ru.shemplo.conduit.appserver.entities.StudyPeriodEntity;
import ru.shemplo.conduit.appserver.entities.repositories.GroupEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;

@Service
@RequiredArgsConstructor
public class GroupsService {
    
    private final GroupEntityRepository groupsRepository;
    private final Clock clock;
    
    public Collection <GroupEntity> getGroupsByStudyPeriod (StudyPeriodEntity studyPeriod) {
        return groupsRepository.findByPeriod (studyPeriod);
    }
    
    public GroupEntity createGroup (String name, String description,
            StudyPeriodEntity period, WUser user) {
        final GroupEntity entity = new GroupEntity ();
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommiter (user.getEntity ());
        entity.setDescription (description);
        entity.setPeriod (period);
        entity.setName (name);
        
        return groupsRepository.save (entity);
    }
    
}
