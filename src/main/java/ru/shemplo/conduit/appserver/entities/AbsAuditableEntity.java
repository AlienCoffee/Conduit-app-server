package ru.shemplo.conduit.appserver.entities;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@ToString
@Getter @Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
public abstract class AbsAuditableEntity extends AbsEntity {
    
    @JsonIgnore
    @Column (nullable = false)
    protected LocalDateTime issued;
    
    @ManyToOne (optional = false)
    protected UserEntity committer;
    
    @JsonProperty ("issued")
    public String getJSONIssued () {
        return issued.format (RU_DATETIME_FORMAT);
    }
    
}
