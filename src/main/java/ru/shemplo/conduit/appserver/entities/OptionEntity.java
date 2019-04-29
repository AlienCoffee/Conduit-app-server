package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "options")
public class OptionEntity extends AbsEntity {
    
    @Column (nullable = false, unique = true)
    private String name;
    
}
