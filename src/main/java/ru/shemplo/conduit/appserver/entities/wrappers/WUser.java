package ru.shemplo.conduit.appserver.entities.wrappers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.*;
import ru.shemplo.conduit.appserver.services.WUserService;
import ru.shemplo.snowball.stuctures.Pair;

@ToString
public class WUser extends User implements Identifiable {
    
    private static final long serialVersionUID = -6022311839660659826L;
    
    //private static final long FIVE_MINUTES = 5 * 60 * 1000;
    
    @Getter private final UserEntity entity;
    private final WUserService service;
    
    private final Map <PeriodEntity, List <RoleEntity>> roles = new HashMap <> ();
    private final Set <GrantedAuthority> authorities = new LinkedHashSet <> ();
    
    public WUser (UserEntity entity, WUserService service) {
        super (entity.getLogin (), entity.getPassword (), 
               new ArrayList <> () /* stub array */ );
        this.service = service;
        this.entity = entity;
    }
    
    @Override
    public Long getId () { return entity.getId (); }
    
    @Override
    public Set <GrantedAuthority> getAuthorities () {
        if (authorities.isEmpty ()) { 
            reloadAuthorities (); 
        }
        
        return authorities;
    }
    
    @Override
    public String getPassword () {
        // This is done for CACHE support purposes
        // By default password is erased after authorization
        // (it cause problems with re-login because no
        // password in object to compare with request value)
        return entity.getPassword ();
    }
    
    public List <RoleEntity> getRoles (PeriodEntity period) {
        if (roles.isEmpty ()) { reloadAuthorities (); }
        return Collections.unmodifiableList (roles.get (period));
    }
    
    public Set <OptionEntity> getOptions (PeriodEntity period) {
        if (roles.isEmpty ()) { reloadAuthorities (); }
        
        Set <OptionEntity> options = Optional.ofNullable (roles.get (period))
                                   . orElse (Collections.emptyList ()).stream ()
                                   . map     (RoleEntity::getOptions)
                                   . flatMap (Set::stream)
                                   . collect (Collectors.toSet ());
        return Collections.unmodifiableSet (options);
    }
    
    public synchronized Set <GrantedAuthority> reloadAuthorities () {
        authorities.clear ();
        roles.clear ();
        
        roles.putAll (service.getAllUserRoles (entity));
        
        roles.entrySet ().stream ()
        . map (Pair::fromMapEntry)
        . map (pair -> pair.applyS (
            lst -> lst.stream ().map (RoleEntity::getOptions)
                 . flatMap (Set::stream)
                 . collect (Collectors.toSet ()))
        )
        . forEach (pair -> {
            final Long periodID = pair.F.getId ();
            for (OptionEntity entity : pair.S) {
                final Long optionID = entity.getId ();
                
                String authority = String.format ("%d/%d", periodID, optionID);
                authorities.add (new SimpleGrantedAuthority (authority));
            }
        });
        
        return authorities;
    }
    
}
