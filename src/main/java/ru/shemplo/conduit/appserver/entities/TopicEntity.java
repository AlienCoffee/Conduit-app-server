package ru.shemplo.conduit.appserver.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "topics")
public class TopicEntity extends AbsAuditableEntity {
    
    @Column (nullable = false)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @Column (nullable = false)
    private LocalDateTime since;
    
    private LocalDateTime until;
    
    @ManyToMany
    private Set <TeacherPersonalityEntity> authors = new HashSet <> ();
    
}
