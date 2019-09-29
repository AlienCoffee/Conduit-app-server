package ru.shemplo.conduit.appserver.web.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.Named;
import ru.shemplo.snowball.stuctures.Pair;

@Getter
@ToString
@RequiredArgsConstructor
public class WebFormSelect <T extends Named> implements WebFormValue <T> {

    private final List <Pair <String, String>> options = new ArrayList <> ();
    private final T parameter;
    private final String name;
    
    private final boolean required;
    
    public WebFormSelect (String name, T parameter, boolean required, String ... options) {
        this.name = name; addOptions (options);
        this.parameter = parameter;
        this.required = required;
    }
    
    @Override
    public String getRowType () { return "select"; }
    
    public WebFormSelect <T> addOptions (String ... options) {
        Arrays.asList (options).forEach (name -> {
            addOption (name, name);
        });
        
        return this;
    }
    
    public WebFormSelect <T> addOption (String name, String value) {
        return addOption (Pair.mp (name, value));
    }
    
    public WebFormSelect <T> addOption (Pair <String, String> option) {
        options.add (option);
        
        return this;
    }
    
}
