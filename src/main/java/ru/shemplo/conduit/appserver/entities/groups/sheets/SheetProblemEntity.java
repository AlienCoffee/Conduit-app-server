package ru.shemplo.conduit.appserver.entities.groups.sheets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
public class SheetProblemEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private SheetEntity olympiad;
    
    @Column (nullable = false, columnDefinition = "text")
    private String title, content;
    
    @ManyToMany
    @LazyCollection (LazyCollectionOption.FALSE)
    private List <FileEntity> attachments = new ArrayList <> ();
    
    @Column (nullable = false)
    private Integer cost;
    
    private Double difficulty;
    
}
