package ru.shemplo.conduit.appserver.entities.wrappers;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.Identifiable;
import ru.shemplo.conduit.appserver.entities.UserEntity;

@ToString
@EqualsAndHashCode (callSuper = false)
public class WUser extends User implements Identifiable {
    
    private static final long serialVersionUID = -6022311839660659826L;
    
    @Getter
    private static final WUser stubUser;
    
    static {
        UserEntity entity = new UserEntity ("guest", "", "", false);
        entity.setId (0L);
        
        stubUser = new WUser (entity);
    }
    
    @Getter protected final UserEntity entity;
    
    public WUser (UserEntity entity) {
        super (entity.getLogin (), entity.getPassword (), new ArrayList <> () /* stub array */ );
        this.entity = entity;
    }
    
    @Override
    public Long getId () { return entity.getId (); }
    
    @Override @Deprecated
    public Collection <GrantedAuthority> getAuthorities () {
        return super.getAuthorities ();
    }
    
    @Override
    public String getPassword () {
        // This is done for CACHE support purposes
        // By default password is erased after authorization
        // (it cause problems with re-login because no
        // password in object to compare with request value)
        return entity.getPassword ();
    }
    
}
