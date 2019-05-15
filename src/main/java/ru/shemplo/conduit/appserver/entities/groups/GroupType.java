package ru.shemplo.conduit.appserver.entities.groups;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.Named;

@RequiredArgsConstructor
public enum GroupType implements Named {
    
    POOL        ("Pool"),        // just set of members
    INFO        ("Info"),        // possible to send information messages (a.k.a. news channel)
    ELIMINATION ("Elimination"), // possible to arrange tournaments or games + all previous
    STUDY       ("Study");       // possible to arrange study process (topics) + all previous
    
    @Getter private final String name;
    
}
