package ru.shemplo.conduit.ts.generator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target (TYPE)
@Retention (RUNTIME)
public @interface DTOType {
    
    String [] interfaces () default {};
    
    String superclass () default "";
    
    String [] code () default {};
    
    boolean generateTypeAssignment () default false;
    
}
