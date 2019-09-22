package ru.shemplo.conduit.appserver.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.shemplo.conduit.appserver.entities.groups.GroupEntity;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetEntity;

public interface OlympiadEntityRepository extends AbsEntityRepository <SheetEntity> {
    
    @Query ("SELECT id FROM OlympiadEntity")
    public List <Long> findAllIds ();
    
    @Query ("SELECT id FROM OlympiadEntity WHERE group_id = :group")
    public List <Long> findIdsByGroup (GroupEntity group);
    
}
