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
@Table (name = "verification_codes")
public class VerificationEntity extends AbsAuditableEntity {
    
    @Column (nullable = false, unique = true)
    private String login, phone;
    
    @Column (nullable = false)
    private String checksum;
    
}
