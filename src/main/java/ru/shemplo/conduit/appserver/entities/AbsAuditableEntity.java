package ru.shemplo.conduit.appserver.entities;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import ru.shemplo.conduit.appserver.start.DBTemplateConstant;
import ru.shemplo.conduit.appserver.start.DBTemplateField;

@ToString
@Getter @Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true)
public abstract class AbsAuditableEntity extends AbsEntity {
    
    @JsonIgnore
    @DBTemplateConstant
    @DBTemplateField ("{now}")
    @Column (nullable = false)
    protected LocalDateTime issued;
    
    @ManyToOne (optional = false)
    protected UserEntity committer;
    
    @Column (nullable = false, columnDefinition = "text")
    protected String comment = "";
    
    @JsonProperty ("issued")
    public String getJSONIssued () {
        return issued.format (RU_DATETIME_FORMAT);
    }
    
}
