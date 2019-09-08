package ru.shemplo.conduit.appserver.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "blog_posts")
@EqualsAndHashCode (callSuper = true)
public class BlogPostEntity extends AbsAuditableEntity {
    
    @Column (nullable = false, columnDefinition = "text")
    private String title, content;
    
    @Column (nullable = false)
    private LocalDateTime published;
    
    @Column (name = "channel")
    @ElementCollection (fetch = FetchType.EAGER)
    private Set <String> channels = new HashSet <> ();
    
    @ManyToMany (fetch = FetchType.EAGER)
    private List <FileEntity> attachments = new ArrayList <> ();
    
}
