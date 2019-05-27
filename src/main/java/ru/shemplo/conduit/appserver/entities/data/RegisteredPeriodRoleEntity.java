package ru.shemplo.conduit.appserver.entities.data;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;
import ru.shemplo.conduit.appserver.entities.AssignmentStatus;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "registered_period_roles", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"user_id", "period_id", "template"})
})
public class RegisteredPeriodRoleEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private UserEntity user;
    
    @ManyToOne (optional = false)
    private PeriodEntity period;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private PersonalDataTemplate template;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private AssignmentStatus status;
    
}
