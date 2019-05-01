package ru.shemplo.conduit.appserver.utils;

import javax.validation.ValidationException;

public class PhoneValidator {
    
    public static String validate (String phone) throws ValidationException {
        return phone;
    }
    
    public static String format (String phone) {
        return phone.replaceAll ("[- \\(\\)\\+]", "");
    }
    
}
