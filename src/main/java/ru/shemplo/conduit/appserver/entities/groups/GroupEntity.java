package ru.shemplo.conduit.appserver.entities.groups;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "groups", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"period_id", "name"})
})
public class GroupEntity extends AbsEntity {
    
    @Column (nullable = false)
    private String name;
    
    @ManyToOne (optional = false)
    private PeriodEntity period;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private GroupType type;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private GroupJoinType joinType;
    
    @ManyToOne (optional = false)
    private UserEntity head;
    
}
