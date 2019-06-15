package ru.shemplo.conduit.appserver.entities;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "files")
@EqualsAndHashCode (callSuper = true)
public class FileEntity extends AbsAuditableEntity {
    
    @Column (nullable = false)
    private String name;
    
    @Column (nullable = false)
    private String path;
    
    @JsonIgnore
    public File toFile () {
        return new File (path);
    }
    
}
