package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "movements")
public class StudentMovementEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private StudentPersonalityEntity student;
    
    @ManyToOne
    private GroupEntity from;
    
    @ManyToOne (optional = false)
    private GroupEntity to;
    
}
