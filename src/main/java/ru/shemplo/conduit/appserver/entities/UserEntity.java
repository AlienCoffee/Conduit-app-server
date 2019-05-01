package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString (exclude = {"roles", "password"})
@ToString (exclude = {"password"})
@Table (name = "users", indexes = {
    @Index (columnList = "phone")
})
@EqualsAndHashCode (callSuper = true)
public class UserEntity extends AbsEntity {
    
    @Column (unique = true)
    private String login;
    
    @Column (nullable = false, unique = true)
    private String phone;
    
    @JsonIgnore
    private String password;
    
    private boolean admin;
    
    /*
    @JsonIgnore
    @ManyToMany (fetch = FetchType.EAGER)
    private Set <RoleEntity> roles = new HashSet <> ();
    */
    
}
