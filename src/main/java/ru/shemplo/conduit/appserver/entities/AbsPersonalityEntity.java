package ru.shemplo.conduit.appserver.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.*;

@ToString
@Getter @Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbsPersonalityEntity extends AbsEntity {
    
    @ManyToOne (fetch = FetchType.EAGER, optional = false)
    @NonNull private UserEntity user;
    
    @ManyToOne (fetch = FetchType.EAGER, optional = false)
    @NonNull private StudyPeriodEntity period;
    
    @Column (nullable = false)
    protected String firstName, lastName;
    
    protected String secondName;
    
    protected String gender;
    
    protected String city, region;
    
    protected LocalDate birthday;
    
}
