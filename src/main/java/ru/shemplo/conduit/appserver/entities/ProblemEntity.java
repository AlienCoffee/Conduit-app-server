package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "problems")
public class ProblemEntity extends AbsAuditableEntity {
    
    @Column (nullable = false)
    private String name;
    
    @Column (columnDefinition = "text")
    private String description;
    
    @Column (nullable = false, columnDefinition = "text")
    private String content;
    
    // attachments
    
}
