package ru.shemplo.conduit.appserver.web.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import ru.shemplo.conduit.appserver.web.form.WebFormField;
import ru.shemplo.conduit.appserver.web.form.WebFormField.WebFormFieldType;
import ru.shemplo.conduit.appserver.web.form.WebFormRow;
import ru.shemplo.conduit.appserver.web.form.WebFormSelect;
import ru.shemplo.conduit.appserver.web.form.WebFormTitle;
import ru.shemplo.conduit.ts.generator.DTOType;
import ru.shemplo.snowball.utils.MiscUtils;

@Getter
@DTOType
public class WebFormRowDTO {
    
    private final List <Object> options = new ArrayList <> ();
    private final WebFormFieldType fieldType;
    private final String rowType;
    private final String title;
    
    private final boolean required;
    private final String comment;
    private final String icon;
    private final String id;
    
    public WebFormRowDTO (WebFormRow row) {
        rowType = row.getRowType ();
        
        switch (rowType) {
            case "title": {
                fieldType = WebFormFieldType.UNDEFINED;
                required = false;
                comment = null;
                id = null;
                
                WebFormTitle title = MiscUtils.cast (row);
                this.title = title.getTitle ();
                icon = title.getIcon ();
            } break;
            
            case "field": {
                icon = null;
                
                WebFormField <?> field = MiscUtils.cast (row);
                required = field.isRequired ();
                id = field.getParameterName ();
                comment = field.getComment ();
                fieldType = field.getType ();
                title = field.getTitle ();
            } break;
            
            case "select": {
                fieldType = WebFormFieldType.UNDEFINED;
                comment = null;
                icon = null;
                
                WebFormSelect <?> select = MiscUtils.cast (row);
                options.addAll (select.getOptions ());
                required = select.isRequired ();
                id = select.getParameterName ();
                this.title = select.getName ();
            } break;
            
            default: {
                String message = "Unknown web form row type: " + rowType;
                throw new IllegalArgumentException (message);
            }
        }
    }
    
}
