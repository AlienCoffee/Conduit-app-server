package ru.shemplo.conduit.appserver.entities.groups.sheets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.*;
import ru.shemplo.conduit.appserver.entities.AbsAuditableEntity;
import ru.shemplo.conduit.appserver.entities.FileEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "olympiad_attempts")
@EqualsAndHashCode (callSuper = true)
public class SheetAttemptEntity extends AbsAuditableEntity {
    
    @ManyToOne (optional = false)
    private SheetEntity olympiad;
    
    @ManyToOne (optional = false)
    private UserEntity user;
    
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private SheetAttemptStatus status;
    
    private String reason;
    
    @ManyToMany ()
    @LazyCollection (LazyCollectionOption.FALSE)
    private List <FileEntity> attachments = new ArrayList <> ();
    
}
