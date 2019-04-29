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
@Table (name = "students_personality")
public class StudentPersonalityEntity extends AbsPersonalityEntity {
    
    private String school;
    
    private int form;
    
    @Column (nullable = false)
    private String motherFirstName, motherLastName;
    private String motherSecondName;
    
    @Column (nullable = false)
    private String fatherFirstName, fatherLastName;
    private String fatherSecondName;
    
}
