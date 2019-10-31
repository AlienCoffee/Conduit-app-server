package ru.shemplo.conduit.appserver.entities;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.shemplo.conduit.ts.generator.DTOType;

@DTOType
@AllArgsConstructor
public enum PeriodStatus implements Named {
    
    CREATED      ("Created"), 
    REGISTRATION ("Registration"), 
    PENDING      ("Pending"), 
    RUNNING      ("Running"), 
    FINISHED     ("Finished");
    
    @Getter
    private final String name;
    
    public static List <PeriodStatus> getValues () {
        return Arrays.asList (values ());
    }
    
}
