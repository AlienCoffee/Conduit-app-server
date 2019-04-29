package ru.shemplo.conduit.appserver.entities.wrappers;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Documented
@AuthenticationPrincipal
@Target ({ PARAMETER, TYPE })
@Retention (RetentionPolicy.RUNTIME)
public @interface IndentifiedUser {
    
}
