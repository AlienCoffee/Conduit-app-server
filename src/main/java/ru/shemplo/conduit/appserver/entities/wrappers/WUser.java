package ru.shemplo.conduit.appserver.entities.wrappers;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.Identifiable;
import ru.shemplo.conduit.appserver.entities.RoleEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.services.WUserService;

@ToString
public class WUser extends User implements Identifiable {
    
    private static final long serialVersionUID = -6022311839660659826L;
    
    @Getter private final UserEntity entity;
    private final Set <RoleEntity> roles;
    private final WUserService service;
    
    public WUser (UserEntity entity, WUserService service) {
        super (entity.getLogin (), entity.getPassword (), 
               new ArrayList <> ());
        this.roles = new HashSet <> ();
        this.service = service;
        this.entity = entity;
    }
    
    @Override
    public Long getId () { return entity.getId (); }
    
    public Collection <RoleEntity> getRoles () {
        return Collections.unmodifiableSet (roles);
    }
    
    @Override
    public Collection <GrantedAuthority> getAuthorities () {
        return super.getAuthorities ();
    }
    
    public static Collection <GrantedAuthority> getAuthorities (UserEntity entity, 
            WUserService service) {
        final List <GrantedAuthority> result = new ArrayList <> ();
        if (entity == null || service == null) { return result; }
        
        service.getAllUsersRoles (entity).stream ().forEach (pair -> {
            final Long periodID = pair.F.getId ();
            
            pair.S.getOptions ().forEach (option -> {
                final String optionName = option.getName ();
                
                String auth = String.format ("%d/%s", periodID, optionName);
                result.add (new SimpleGrantedAuthority (auth));
            });
        });
        
        return result;
    }
    
}
