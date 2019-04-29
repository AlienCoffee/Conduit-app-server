package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "problems_assignments")
public class ProblemAssignmentEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private GroupEntity group;
    
    @ManyToOne (optional = false)
    private ProblemEntity problem;
    
    @Column (nullable = false)
    private String action; // add | remove | explain
    
    private String value; // f.e. cost of problem (in case of add)
    
}
