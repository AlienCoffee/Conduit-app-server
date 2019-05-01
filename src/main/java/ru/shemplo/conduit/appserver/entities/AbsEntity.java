package ru.shemplo.conduit.appserver.entities;

import javax.persistence.*;

import lombok.*;

@ToString
@Getter @Setter
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public abstract class AbsEntity implements Identifiable {
    
    @Access (AccessType.PROPERTY)
    @GeneratedValue @Id protected Long id;
    
}
