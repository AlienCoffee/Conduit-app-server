package ru.shemplo.conduit.appserver.entities.groups.sheets;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "olympiads", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"group_id", "name"})
})
public class SheetEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private GroupEntity group;
    
    @Column (nullable = false)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @Column (nullable = false)
    private LocalDateTime published, finished;
    
    @Column (nullable = false, columnDefinition = "int(11) default 2")
    private Integer attemptsLimit = 2;
    
    @Column (nullable = false)
    private boolean resultsFinalized = false;
    
    @Column (nullable = false)
    private boolean resultsVisible = true;
    
}
