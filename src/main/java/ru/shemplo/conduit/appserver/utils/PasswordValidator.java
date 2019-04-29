package ru.shemplo.conduit.appserver.utils;

import javax.validation.ValidationException;

public class PasswordValidator {
    
    public static String validate (String password) throws ValidationException {
        if (password.trim ().length () < 6) {
            String messages = String.format (
                "At least 6 characters required (%d given)", 
                password.length ()
            );
            
            throw new ValidationException (messages);
        }
        
        return password.trim ();
    }
    
}
