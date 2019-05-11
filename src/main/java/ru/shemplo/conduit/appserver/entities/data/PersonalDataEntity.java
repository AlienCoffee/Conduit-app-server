package ru.shemplo.conduit.appserver.entities.data;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
@Table (name = "personal_data", uniqueConstraints = {
    @UniqueConstraint (columnNames = {"user_id", "period_id", "type"})
})
public class PersonalDataEntity extends AbsEntity {
    
    @ManyToOne (optional = false)
    private UserEntity user;
    
    @ManyToOne (optional = false)
    private PeriodEntity period;
    
    /**
     * Special marker for DB entry:
     * - if student parents are not registered they don't need to do this
     * - if they want to register then DB entry can be just re-assigned
     */
    @Column (nullable = false)
    private PersonalDataType type;
    
    @Column (nullable = false)
    private String firstName, lastName;
    private String secondName;
    
    /**
     * M - male,
     * F - female
     */
    @Column (nullable = false)
    private char gender;
    
    @Column (nullable = false)
    private LocalDate birthday;
    
    @Column (nullable = false)
    private String livingRegion, livingCity;
    @Column (columnDefinition = "text", nullable = false)
    private String livingAddress;
    private String studyRegion, studyCity;
    
    /**
     * For scholars it is theirs' schools.
     * For teachers it is schools or universities
     */
    @Column (columnDefinition = "text")
    private String studyInstitution;
    private String studyInstitutionCode;
    
    private int form;
    
    private String qualification;
    
    @Column (nullable = false)
    private String idDocSeries, idDocNumber;
    @Column (columnDefinition = "text")
    private String idDocSource;
    private String idDocSourceCode;
    private LocalDate idDocIssued;
    
    @Column (nullable = false)
    private String medPolicyNumber;
    
    private String workingPlace, workingPosition;
    
    private String extraPhone, extraEmail;
    
}
