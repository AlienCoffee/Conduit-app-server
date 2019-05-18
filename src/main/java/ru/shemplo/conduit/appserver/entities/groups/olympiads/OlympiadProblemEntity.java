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
@EqualsAndHashCode (callSuper = true)
@Table (name = "olympiad_problems", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"olympiad_id", "title"})
})
public class OlympiadProblemEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private OlympiadEntity olympiad;
    
    @Column (nullable = false, columnDefinition = "text")
    private String title, content;
    
    @ManyToMany (fetch = FetchType.EAGER)
    private List <FileEntity> attachments = new ArrayList <> ();
    
    @Column (nullable = false)
    private Integer cost;
    
    private Double difficulty;
    
}
