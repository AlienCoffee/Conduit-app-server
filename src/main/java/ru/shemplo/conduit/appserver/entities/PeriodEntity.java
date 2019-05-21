package ru.shemplo.conduit.appserver.entities;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "periods")
@EqualsAndHashCode (callSuper = true)
public class PeriodEntity extends AbsAuditableEntity {
    
    @Getter private static PeriodEntity system;
    
    public static synchronized void setSystem (PeriodEntity period) {
        if (system == null) { system = period; }
    }
    
    @Column (nullable = false, unique = true)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @JsonIgnore
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
