package ru.shemplo.conduit.appserver.web.controllers;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.shemplo.conduit.appserver.web.ResponseBox;

@RestControllerAdvice
public class ExceptionController {
    
    @ExceptionHandler (SecurityException.class)
    public ResponseBox <Void> handleSecurityException (SecurityException exception) {
        return ResponseBox.fail (exception);
    }
    
}
