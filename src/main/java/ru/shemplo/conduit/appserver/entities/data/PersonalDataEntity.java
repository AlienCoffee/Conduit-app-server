package ru.shemplo.conduit.appserver.entities.data;

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
    @UniqueConstraint (columnNames = {"user_id", "period_id", "type", "field"})
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
    @Enumerated (EnumType.STRING)
    private PersonalDataField field;
    
    @Column (nullable = false, columnDefinition = "text")
    private String value;
    
    public <R> R deserialize () { return field.deserialize (value); }
    
}
