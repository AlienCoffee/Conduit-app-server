package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "options")
@EqualsAndHashCode (callSuper = true)
public class OptionEntity extends AbsEntity implements Named {
    
    @Column (nullable = false, unique = true)
    private String name;
    
}
