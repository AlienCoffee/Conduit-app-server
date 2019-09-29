package ru.shemplo.conduit.appserver.web.form;

import ru.shemplo.conduit.appserver.entities.Named;

public interface WebFormValue <T extends Named> extends WebFormRow {
    
    public T getParameter ();
    
    public boolean isRequired ();
    
    default public String getParameterName () {
        return getParameter ().getName ();
    }
    
}
