package ru.shemplo.conduit.appserver.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "groups")
public class GroupEntity extends AbsAuditableEntity {
    
    @ManyToOne @JsonIgnore
    private StudyPeriodEntity period;
    
    @Column (nullable = false)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @ManyToMany @JsonIgnore
    private Set <TeacherPersonalityEntity> teachers = new HashSet <> ();
    
    @ManyToMany @JsonIgnore
    private Set <TopicEntity> topics = new HashSet <> ();
    
}
