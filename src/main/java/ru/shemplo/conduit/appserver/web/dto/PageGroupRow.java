package ru.shemplo.conduit.appserver.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

@ToString
@Getter @Setter
@RequiredArgsConstructor
public class PageGroupRow {
    
    private final GroupEntity group;
    
    private boolean assigned = false;
    private boolean applied  = false;
    
    
}
