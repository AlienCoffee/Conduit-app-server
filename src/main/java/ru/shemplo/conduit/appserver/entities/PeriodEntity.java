package ru.shemplo.conduit.appserver.entities;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.start.DBTemplateAnchor;
import ru.shemplo.conduit.appserver.start.DBTemplateConstant;
import ru.shemplo.conduit.kt.generator.KTEntity;
import ru.shemplo.conduit.ts.generator.DTOType;

@Entity
@KTEntity
@Getter @Setter
@NoArgsConstructor
@DTOType (code = {
    "public started : boolean;",
    "public html : HTMLElement"
}, generateTypeAssignment = true)
@AllArgsConstructor
@Table (name = "periods")
@ToString (callSuper = true)
@EqualsAndHashCode (callSuper = true)
public class PeriodEntity extends AbsAuditableEntity {
    
    @Getter private static PeriodEntity system;
    
    public static synchronized void setSystem (PeriodEntity period) {
        if (system == null) { system = period; }
    }
    
    public static PeriodEntity getSystemForKT () { return system; }
    
    @DBTemplateAnchor
    @Column (nullable = false, unique = true)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    //@JsonIgnore
    @DBTemplateConstant
    @Column (nullable = false)
    private LocalDateTime since;
    
    //@JsonIgnore
    private LocalDateTime until;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private PeriodStatus status;
    
    /*
    @JsonProperty ("since")
    public String getJSONSince () {
        since.format (RU_DATETIME_FORMAT);
        return since.toString ();
    }
    
    @JsonProperty ("until")
    public String getJSONUntil () {
        if (until == null) { return ""; }
        //return until.format (RU_DATETIME_FORMAT);
        return until.toString ();
    }
    */
    
}
