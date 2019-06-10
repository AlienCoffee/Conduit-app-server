package ru.shemplo.conduit.appserver.web.controllers;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.web.ResponseBox;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    
    @ExceptionHandler (SecurityException.class)
    public ResponseBox <Void> handleSException (SecurityException exception) {
        log.error ("[SECURITY] {}", exception.toString ());
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (EntityNotFoundException.class)
    public ResponseBox <Void> handleENFException (EntityNotFoundException exception) {
        log.error ("[EXISTANCE] {}", exception.toString ());
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (EntityExistsException.class)
    public ResponseBox <Void> handleEEException (EntityExistsException exception) {
        log.error ("[EXISTANCE] {}", exception.toString ());
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (IllegalStateException.class)
    public ResponseBox <Void> handleISException (IllegalStateException exception) {
        log.error ("[PROGRAM STATE] {}", exception.toString ());
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (IllegalArgumentException.class)
    public ResponseBox <Void> handleIAException (IllegalArgumentException exception) {
        log.error ("[ARGUMENT] {}", exception.toString ());
        return ResponseBox.fail (exception);
    }
    
    @ExceptionHandler (NullPointerException.class)
    public ResponseBox <Void> handleNPException (NullPointerException exception) {
        log.error ("[NULL POINTER] {}", exception.toString ());
        return ResponseBox.fail (exception);
    }
    
}
