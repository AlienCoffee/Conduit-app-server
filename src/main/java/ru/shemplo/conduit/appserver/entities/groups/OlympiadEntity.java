package ru.shemplo.conduit.appserver.entities.groups;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;
import ru.shemplo.conduit.appserver.entities.FileEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "group_posts", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"group_id", "name"})
})
public class OlympiadEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private GroupEntity group;
    
    @Column (nullable = false)
    private String name;
    
    @Column (nullable = false)
    private LocalDateTime published;
    
    @ManyToMany
    private List <FileEntity> conditions;
    
}
