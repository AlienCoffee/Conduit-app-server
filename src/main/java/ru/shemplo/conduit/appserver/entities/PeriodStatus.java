package ru.shemplo.conduit.appserver.entities;

import lombok.AllArgsConstructor;
import ru.shemplo.conduit.ts.generator.DTOType;

@DTOType
@AllArgsConstructor
public enum PeriodStatus {
    
    CREATED, REGISTRATION, PENDING, RUNNING, FINISHED;
    
}
