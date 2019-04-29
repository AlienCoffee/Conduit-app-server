package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "teachrs_personality")
public class TeacherPersonalityEntity extends AbsPersonalityEntity {
    
    @Column (nullable = false)
    private String qualification;
    
    private String phone2;
    
}
