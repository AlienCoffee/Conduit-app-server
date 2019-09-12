package ru.shemplo.conduit.appserver.entities;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import ru.shemplo.conduit.appserver.start.DBTemplateAnchor;
import ru.shemplo.conduit.appserver.start.DBTemplateConstant;
import ru.shemplo.conduit.ts.generator.DTOType;

@Entity
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
    
    @DBTemplateAnchor
    @Column (nullable = false, unique = true)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @JsonIgnore
    @DBTemplateConstant
    @Column (nullable = false)
    private LocalDateTime since;
    
    @JsonIgnore
    private LocalDateTime until;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private PeriodStatus status;
    
    @JsonProperty ("since")
    public String getJSONSince () {
        return since.format (RU_DATETIME_FORMAT);
    }
    
    @JsonProperty ("until")
    public String getJSONUntil () {
        if (until == null) { return ""; }
        return until.format (RU_DATETIME_FORMAT);
    }
    
}
