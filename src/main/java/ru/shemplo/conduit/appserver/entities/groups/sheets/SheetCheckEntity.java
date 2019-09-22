package ru.shemplo.conduit.appserver.entities.groups.sheets;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "olympiad_checks", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"problem_id", "attempt_id", "committer_id"})
})
public class SheetCheckEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private SheetProblemEntity problem;
    
    @ManyToOne (optional = false)
    private SheetAttemptEntity attempt;
    
    @Column (nullable = false)
    private Integer points;
    
}
