package ru.shemplo.conduit.appserver.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataTemplate;
import ru.shemplo.conduit.appserver.start.DBTemplateAnchor;
import ru.shemplo.conduit.kt.generator.KTEntity;

@Entity
@KTEntity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString (callSuper = true)
@Table (name = "roles", indexes = {
    @Index (columnList = "name", unique = true)
})
public class RoleEntity extends AbsEntity {
    
    public  static final String HEAD_ROLE = "HEAD";
    private static final String ROLE_ = "ROLE_";
    
    @DBTemplateAnchor
    @Column (nullable = false, unique = true)
    private String name;
    
    public String getRoleName () {
        return ROLE_.concat (name);
    }
    
    @ManyToMany (fetch = FetchType.EAGER)
    private Set <OptionEntity> options = new HashSet <> ();
    
    @Enumerated (EnumType.STRING)
    private PersonalDataTemplate template;
    
}
