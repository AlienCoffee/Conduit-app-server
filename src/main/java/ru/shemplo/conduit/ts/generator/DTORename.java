package ru.shemplo.conduit.ts.generator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// TODO: now supported only fields rename in DTO generator and parameters in API generator

@Retention (RUNTIME)
@Target ({ FIELD, PARAMETER, METHOD })
public @interface DTORename {
    
    String value ();
    
}
