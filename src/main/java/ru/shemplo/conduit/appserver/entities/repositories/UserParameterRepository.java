package ru.shemplo.conduit.appserver.entities.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.shemplo.conduit.appserver.entities.UserParameter;
import ru.shemplo.conduit.appserver.entities.UserParameterName;

public interface UserParameterRepository extends AbsEntityRepository <UserParameter> {
    
    @Query ("SELECT ent.id FROM UserParameter ent WHERE user.id = :userId AND ent.parameter = :parameter")
    public Long findIdByUser_IdAndParameter (@Param ("userId") Long userId, 
        @Param ("parameter") UserParameterName parameter
    );
    
}
