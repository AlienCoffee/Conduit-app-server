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
@Table (name = "guard_rules")
public class GuardRuleEntity extends AbsEntity {
    
    @Column (nullable = false, unique = true)
    private String object;
    
    @ManyToMany (fetch = FetchType.EAGER)
    private Set <OptionEntity> requirements = new HashSet <> ();
    
}
