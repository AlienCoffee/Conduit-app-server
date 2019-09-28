package ru.shemplo.conduit.appserver.web.dto;

import lombok.Getter;
import ru.shemplo.conduit.appserver.web.form.WebFormField;
import ru.shemplo.conduit.appserver.web.form.WebFormField.WebFormFieldType;
import ru.shemplo.conduit.appserver.web.form.WebFormRow;
import ru.shemplo.conduit.appserver.web.form.WebFormTitle;
import ru.shemplo.conduit.ts.generator.DTOType;
import ru.shemplo.snowball.utils.MiscUtils;

@Getter
@DTOType
public class WebFormRowDTO {
    
    private final WebFormFieldType fieldType;
    private final String rowType;
    private final String title;
    
    private final boolean required;
    private final String comment;
    private final String icon;
    
    public WebFormRowDTO (WebFormRow row) {
        rowType = row.getRowType ();
        
        switch (rowType) {
            case "title": {
                fieldType = WebFormFieldType.UNDEFINED;
                required = true;
                comment = null;
                
                WebFormTitle title = MiscUtils.cast (row);
                this.title = title.getTitle ();
                icon = title.getIcon ();
            } break;
            
            case "field": {
                icon = null;
                
                WebFormField <?> field = MiscUtils.cast (row);
                required = field.isRequired ();
                comment = field.getComment ();
                fieldType = field.getType ();
                title = field.getTitle ();
            } break;
            
            default: {
                String message = "Unknown web form row type: " + rowType;
                throw new IllegalArgumentException (message);
            }
        }
    }
    
}
