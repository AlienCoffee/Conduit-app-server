package ru.shemplo.conduit.appserver.web.controllers;

import javax.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.shemplo.conduit.appserver.web.ResponseBox;

@RestControllerAdvice
public class ExceptionController {
    
    @ExceptionHandler (SecurityException.class)
    public ResponseBox <Void> handleSException (SecurityException exception) {
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (EntityNotFoundException.class)
    public ResponseBox <Void> handleENFException (EntityNotFoundException exception) {
        return ResponseBox.fail (exception);
    }
    
}
