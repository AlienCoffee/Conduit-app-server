package ru.shemplo.conduit.appserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;
import ru.shemplo.conduit.appserver.start.DBTemplateAnchor;
import ru.shemplo.conduit.ts.generator.DTOType;

@Entity
@DTOType
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "options")
@ToString (callSuper = true)
@EqualsAndHashCode (callSuper = true)
public class OptionEntity extends AbsEntity implements Named {
    
    @DBTemplateAnchor
    @Column (nullable = false, unique = true)
    private String name;
    
}
