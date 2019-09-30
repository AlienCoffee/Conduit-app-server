package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import ru.shemplo.conduit.appserver.start.DBTemplateAnchor;
import ru.shemplo.conduit.appserver.start.DBTemplateConstant;
import ru.shemplo.conduit.kt.generator.KTEntity;
import ru.shemplo.conduit.ts.generator.DTOType;

@Entity
@DTOType
@KTEntity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString (exclude = {"roles", "password"})
@Table (name = "users", indexes = {
    @Index (columnList = "phone")
})
@EqualsAndHashCode (callSuper = true)
@ToString (exclude = {"password"}, callSuper = true)
public class UserEntity extends AbsEntity {
    
    @Getter private static UserEntity adminEntity;
    
    public static synchronized void setAdmin (UserEntity entity) {
        if (adminEntity == null) { adminEntity = entity; }
    }
    
    @DBTemplateAnchor
    @Column (nullable = false, unique = true)
    private String login;
    
    @Column (nullable = false, unique = true)
    private String phone;
    
    @JsonIgnore
    @DBTemplateConstant
    private String password;
    
    @Column (nullable = false)
    private Boolean isAdmin = false;
    
}
