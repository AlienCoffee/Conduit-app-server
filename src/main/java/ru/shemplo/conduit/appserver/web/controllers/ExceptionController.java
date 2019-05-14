package ru.shemplo.conduit.appserver.web.controllers;

import javax.persistence.EntityExistsException;
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
    
    @ExceptionHandler (EntityExistsException.class)
    public ResponseBox <Void> handleEEException (EntityExistsException exception) {
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (IllegalStateException.class)
    public ResponseBox <Void> handleISException (IllegalStateException exception) {
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (IllegalArgumentException.class)
    public ResponseBox <Void> handleIAException (IllegalArgumentException exception) {
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (NullPointerException.class)
    public ResponseBox <Void> handleNPException (NullPointerException exception) {
        return ResponseBox.fail (exception);
    }
    
}
