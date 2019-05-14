package ru.shemplo.conduit.appserver.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;

@Getter
@ToString
@RequiredArgsConstructor
public class GroupMember {
    
    private final UserEntity user;
    
    private final RoleEntity role;
    
}
