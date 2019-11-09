package ru.shemplo.conduit.appserver.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "users_parameters", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"user_id", "parameter"})
})
public class UserParameter extends AbsEntity {
    
    @ManyToOne (optional = false)
    private UserEntity user;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private UserParameterName parameter;
    
    @Column (nullable = false)
    private String value;
    
}
