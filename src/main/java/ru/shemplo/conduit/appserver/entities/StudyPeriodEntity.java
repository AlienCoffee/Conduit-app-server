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
@Table (name = "study_periods")
@EqualsAndHashCode (callSuper = true)
public class StudyPeriodEntity extends AbsAuditableEntity {
    
    @Column (nullable = false)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @JsonIgnore
    @Column (nullable = false)
    private LocalDateTime since;
    
    @JsonIgnore
    private LocalDateTime until;
    
    private boolean active;
    
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
