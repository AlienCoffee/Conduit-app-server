package ru.shemplo.conduit.appserver.utils;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marker annotation to show that method should be optimized in future.
 * 
 * @author Shemplo
 *
 */
@Target (METHOD)
@Retention (SOURCE)
public @interface NotEffectiveMethod {
    
    public String [] value () default "";
    
}
