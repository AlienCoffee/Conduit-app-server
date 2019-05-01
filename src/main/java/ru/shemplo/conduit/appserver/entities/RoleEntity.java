package ru.shemplo.conduit.appserver.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "roles", indexes = {
    @Index (columnList = "name", unique = true)
})
public class RoleEntity extends AbsEntity {
    
    public  static final String HEAD_ROLE = "HEAD";
    private static final String ROLE_ = "ROLE_";
    
    @Column (nullable = false, unique = true)
    private String name;
    
    public String getRoleName () {
        return ROLE_.concat (name);
    }
    
    @ManyToMany (fetch = FetchType.EAGER)
    private Set <OptionEntity> options = new HashSet <> ();
    
    @Deprecated
    private String personality;
    
}
