package ru.shemplo.conduit.appserver.start;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Retention (RUNTIME)
@Target (FIELD)
public @interface DBTemplateField {
    
    public String value ();
    
}
