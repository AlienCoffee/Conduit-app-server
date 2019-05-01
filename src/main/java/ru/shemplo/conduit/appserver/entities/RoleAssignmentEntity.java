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
@Table (name = "role_assignments")
public class RoleAssignmentEntity extends AbsAuditableEntity {
    
    @ManyToOne
    private UserEntity user;
    
    @ManyToOne
    private PeriodEntity period;
    
    @ManyToOne
    private RoleEntity role;
    
}
