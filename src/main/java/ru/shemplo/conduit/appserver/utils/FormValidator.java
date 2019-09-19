package ru.shemplo.conduit.appserver.utils;

import javax.validation.ValidationException;

public class FormValidator {
    
    public static String validateLogin (String login) throws ValidationException {
        if (login == null || login.trim ().length () < 3) {
            String messages = String.format (
                "At least 3 characters required in login (now just %d)", 
                login != null ? login.length () : 0
            );
            
            throw new ValidationException (messages);
        }
        
        return login.trim ();
    }
    
    public static String validatePhone (String phone) throws ValidationException {
        return phone;
    }
    
    public static String validatePassword (String password) throws ValidationException {
        if (password == null || password.trim ().length () < 8) {
            String messages = String.format (
                "At least 8 characters required in password (now just %d)", 
                password != null ? password.length () : 0
            );
            
            throw new ValidationException (messages);
        }
        
        return password.trim ();
    }
    
    public static String formatPhone (String phone) {
        return phone.replaceAll ("[- \\(\\)\\+]", "");
    }
    
}
