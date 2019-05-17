package ru.shemplo.conduit.appserver.entities.groups.olympiads;

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
@Table (name = "olympiad_attempts")
@EqualsAndHashCode (callSuper = true)
public class OlympiadAttemptEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private OlympiadProblemEntity problem;
    
    @Column (columnDefinition = "text")
    private String comment;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private OlympiadAttemptStatus status;
    
    private String reason;
    
    @ManyToMany
    private List <FileEntity> attachments = new ArrayList <> ();
    
}
