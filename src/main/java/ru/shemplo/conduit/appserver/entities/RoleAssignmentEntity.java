package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "role_assignments")
public class RoleAssignmentEntity extends AbsAuditableEntity {
    
    private UserEntity user;
    
    private StudyPeriodEntity period;
    
    private RoleEntity role;
    
}
