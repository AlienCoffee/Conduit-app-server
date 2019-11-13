package ru.shemplo.conduit.appserver.web.dto;

import java.time.LocalDateTime;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.PeriodStatus;
import ru.shemplo.conduit.kt.generator.KTEntity;
import ru.shemplo.conduit.ts.generator.DTOType;

@KTEntity
@ToString
@Getter @Setter
@NoArgsConstructor
@DTOType (code = {
    "public started : boolean;",
    "public html : HTMLElement"
}, generateTypeAssignment = true)
@AllArgsConstructor
public class PeriodDTO {
    
    private String name, description;
    private LocalDateTime since, until;
    private PeriodStatus status;
    
    public PeriodDTO (PeriodEntity period) {
        name = period.getName ();
        description = period.getDescription ();
        since = period.getSince ();
        until = period.getUntil ();
        status = period.getStatus ();
    }
    
    // ...
    
}
