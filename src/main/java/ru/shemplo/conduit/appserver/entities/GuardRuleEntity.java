package ru.shemplo.conduit.appserver.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.start.DBTemplateAnchor;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString (callSuper = true)
@Table (name = "guard_rules")
public class GuardRuleEntity extends AbsEntity {
    
    @DBTemplateAnchor
    @Column (nullable = false, unique = true)
    private String object;
    
    @Column (nullable = false)
    private Boolean selfAllowed = false;
    
    @ManyToMany (fetch = FetchType.EAGER)
    private Set <OptionEntity> requirements = new HashSet <> ();
    
}
