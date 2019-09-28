package ru.shemplo.conduit.appserver.web.form;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.Named;
import ru.shemplo.conduit.ts.generator.DTOType;

@Getter
@ToString
@RequiredArgsConstructor
public class WebFormField <T extends Named> implements WebFormRow {
    
    private final T field;
    
    private final WebFormFieldType type;
    private final String title, comment;
    private final boolean required;
    
    @DTOType
    public static enum WebFormFieldType {
        TEXT, NUMBER, DATE, UNDEFINED
    }
    
    @Override
    public String getRowType () { return "field"; }
    
    public String getParameterName () {
        return field.getName ();
    }
    
}
