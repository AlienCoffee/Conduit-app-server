package ru.shemplo.conduit.appserver.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.AssignmentStatus;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupJoinType;

@ToString
@Getter @Setter
@RequiredArgsConstructor
public class PageGroupRow {
    
    private final GroupEntity group;
    
    private AssignmentStatus status;
    
    private GroupJoinType joinType;
    
}
