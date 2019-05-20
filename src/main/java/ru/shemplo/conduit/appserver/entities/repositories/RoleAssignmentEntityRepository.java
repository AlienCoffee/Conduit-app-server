package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.RoleAssignmentEntity;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

public interface RoleAssignmentEntityRepository extends AbsEntityRepository <RoleAssignmentEntity> {
    
    default
    public RoleAssignmentEntity getByAll (UserEntity user, PeriodEntity period, RoleEntity role) {
        return findByUserAndPeriodAndRole (user, period, role);
    }
    
    public RoleAssignmentEntity findByUserAndPeriodAndRole (UserEntity user, PeriodEntity period, RoleEntity role);
    
    public List <RoleAssignmentEntity> findByUserAndPeriod (UserEntity user, PeriodEntity period);
    
    public List <RoleAssignmentEntity> findByUser (UserEntity user);
    
    @Query ("SELECT rae.role.id FROM RoleAssignmentEntity rae WHERE rae.period = :period AND rae.user = :user")
    public List <Long> findRolesIdsByPeriodAndUser (PeriodEntity period, UserEntity user);
    
}
