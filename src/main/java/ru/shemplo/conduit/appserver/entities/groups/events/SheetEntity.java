package ru.shemplo.conduit.appserver.entities.groups.events;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;
import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "sheets")
public class SheetEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private GroupEntity group;
    
    @Column (nullable = false)
    private String header, footer;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private SheetCharacter character;
    
    @Column (nullable = false)
    private LocalDateTime published;
    
    private LocalDateTime deadline;
    
}
