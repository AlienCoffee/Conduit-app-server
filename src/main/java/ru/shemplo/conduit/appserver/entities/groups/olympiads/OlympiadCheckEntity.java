package ru.shemplo.conduit.appserver.entities.groups.olympiads;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "olympiad_checks")
@EqualsAndHashCode (callSuper = true)
public class OlympiadCheckEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private OlympiadProblemEntity problem;
    
    @ManyToOne (optional = false)
    private OlympiadAttemptEntity attempt;
    
    @Column (nullable = false)
    private Integer points;
    
}
