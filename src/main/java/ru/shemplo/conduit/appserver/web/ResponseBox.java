package ru.shemplo.conduit.appserver.web;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.ts.generator.DTOType;

@DTOType
@RequiredArgsConstructor
public class ResponseBox <T> {
    
    @Getter private Map <String, Object> params = new HashMap <> ();
    @Getter private final Boolean error ;
    @Getter private final String message;
    @Getter private final T object;
    
    public ResponseBox <T> addParam (String key, Object value) {
        params.put (key, value); return this;
    }
    
    public static <T> ResponseBox <T> ok () {
        return ok ("");
    }
    
    public static <T> ResponseBox <T> ok (String message) {
        return ok (message, null);
    }
    
    public static <T> ResponseBox <T> ok (T object) {
        return ok ("", object);
    }
    
    public static <T> ResponseBox <T> ok (String message, T object) {
        return new ResponseBox <T> (false, message, object);
    }
    
    public static <T> ResponseBox <T> fail (String message) {
        return fail (message, null);
    }
    
    public static <T> ResponseBox <T> fail (Throwable exception) {
        return fail (exception.getMessage (), null);
    }
    
    public static <T> ResponseBox <T> fail (T object) {
        return fail ("", object);
    }
    
    public static <T> ResponseBox <T> fail (String message, T object) {
        return new ResponseBox <T> (true, message, null);
    }
    
}
