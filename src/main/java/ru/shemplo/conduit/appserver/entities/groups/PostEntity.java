package ru.shemplo.conduit.appserver.entities.groups;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Table (name = "posts", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"group_id", "title"})
})
public class PostEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private GroupEntity group;
    
    @Column (nullable = false, columnDefinition = "text")
    private String title, content;
    
    @Column (nullable = false)
    private LocalDateTime published;
    
    @ManyToMany (fetch = FetchType.EAGER)
    private List <FileEntity> attachments = new ArrayList <> ();
    
}
