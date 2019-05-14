package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "role_assignments", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"user_id", "period_id", "role_id"})
})
public class RoleAssignmentEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private UserEntity user;
    
    @ManyToOne (optional = false)
    private PeriodEntity period;
    
    @ManyToOne (optional = false)
    private RoleEntity role;
    
}
