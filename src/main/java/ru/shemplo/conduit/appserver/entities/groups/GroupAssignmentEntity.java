package ru.shemplo.conduit.appserver.entities.groups;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;
import ru.shemplo.conduit.appserver.entities.AssignmentStatus;
import ru.shemplo.conduit.appserver.entities.UserEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "group_assignments", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"user_id", "group_id"})
})
public class GroupAssignmentEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private UserEntity user;
    
    @ManyToOne (optional = false)
    private GroupEntity group;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private AssignmentStatus status;
    
}
