package ru.shemplo.conduit.appserver.web.dto;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.kt.generator.KTEntity;
import ru.shemplo.conduit.ts.generator.DTOType;

@KTEntity
@Getter @Setter
@NoArgsConstructor
@DTOType (code = {
    "public started : boolean;",
    "public html : HTMLElement"
}, generateTypeAssignment = true)
@AllArgsConstructor
@ToString (callSuper = true)
public class PeriodDTO {
    
    private String name, description;
    
    public PeriodDTO (PeriodEntity period) {
        name = period.getName ();
        description = period.getDescription ();
    }
    
    // ...
    
}
