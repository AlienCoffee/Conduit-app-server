package ru.shemplo.conduit.appserver.web.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.data.PersonalDataField;
import ru.shemplo.snowball.stuctures.Pair;

@Getter
@ToString
@RequiredArgsConstructor
public class WebFormSelect implements WebFormRow {

    private final List <Pair <String, String>> options = new ArrayList <> ();
    private final String name, parameterName;
    private final boolean required;
    
    public WebFormSelect (String name, PersonalDataField field, boolean required, String ... options) {
        this.name = name; addOptions (options);
        parameterName = field.getName ();
        this.required = required;
    }
    
    @Override
    public String getRowType () { return "select"; }
    
    public WebFormSelect addOptions (String ... options) {
        Arrays.asList (options).forEach (name -> {
            addOption (name, name);
        });
        
        return this;
    }
    
    public WebFormSelect addOption (String name, String value) {
        return addOption (Pair.mp (name, value));
    }
    
    public WebFormSelect addOption (Pair <String, String> option) {
        options.add (option);
        
        return this;
    }
    
}
