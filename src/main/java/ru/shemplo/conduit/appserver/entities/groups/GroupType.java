package ru.shemplo.conduit.appserver.entities.groups;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.Named;

@RequiredArgsConstructor
public enum GroupType implements Named {
    
    POOL        ("Pool"), 
    INFO        ("Info"), 
    ELIMINATION ("Elimination"), 
    STUDY       ("Study");
    
    @Getter private final String name;
    
}
