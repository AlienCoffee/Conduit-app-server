package ru.shemplo.conduit.appserver.entities.groups.topics;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "topics", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"group_id", "name"})
})
public class TopicEntity extends AbsAuditableEntity {

    @ManyToOne (optional = false)
    private GroupEntity group;
    
    @Column (nullable = false)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @Column (nullable = false)
    private LocalDateTime published, finished;
    
}
