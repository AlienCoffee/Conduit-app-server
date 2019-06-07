package ru.shemplo.conduit.appserver.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import lombok.*;
import ru.shemplo.conduit.appserver.start.DBTemplateField;
import ru.shemplo.conduit.appserver.utils.Utils;
import ru.shemplo.snowball.utils.MiscUtils;

@ToString
@Getter @Setter
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public abstract class AbsEntity implements Identifiable {
    
    @Access (AccessType.PROPERTY) @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY) protected Long id;
    
    public String toTemplateString () {
        String packageName = AbsEntity.class.getPackage ().getName () + ".";
        String entityName = this.getClass ().getName ().replace (packageName, "");
        StringBuilder sb = new StringBuilder (entityName).append ("#");
        sb.append (getEntityTemplateId (this)).append (": ");
        
        List <Field> fields = new ArrayList<> ();
        fields.addAll (Arrays.asList (this.getClass ().getSuperclass ().getDeclaredFields ())); // AbsAuditable | Abs
        fields.addAll (Arrays.asList (this.getClass ().getDeclaredFields ())); // this
        
        if (this instanceof AbsAuditableEntity) {
            Class <?> absEntity = this.getClass ().getSuperclass ().getSuperclass (); // Abs
            fields.addAll (Arrays.asList (absEntity.getDeclaredFields ()));
        }
        
        final AbsEntity entity = this;
        
        String parameters = fields.stream ()
        . filter (f -> !Modifier.isStatic (f.getModifiers ()))
        . filter (f -> !Modifier.isFinal (f.getModifiers ()))
        . map    (field -> {
            DBTemplateField annotation = field.isAnnotationPresent (DBTemplateField.class)
                                       ? field.getAnnotation (DBTemplateField.class)
                                       : null;
            String value = null;
            if (annotation == null || annotation.value ().trim ().length () == 0) {                
                try {
                    field.setAccessible (true);
                    Object object = field.get (entity);
                    if (object instanceof Collection) {
                        Collection <?> collection = MiscUtils.cast (object);
                        value = collection.stream ().map (item -> {
                            if (item instanceof AbsEntity) {
                                final AbsEntity ent = MiscUtils.cast (item);
                                return "#" + getEntityTemplateId (ent);
                            } else {
                                return String.format ("\"%s\"", item.toString ());
                            }
                        }).collect (Collectors.joining (", ", "[", "]"));
                    } else { // it's just a simple value (String, Number, LocalDate, ...)                    
                        value = object == null ? null : "\"" + object.toString () + "\"";
                    }
                } catch (Exception e) {}
            } else {
                value = annotation.value ();
            }
            
            if (value == null) { return null; }
            return String.format ("%s=%s", field.getName (), value);
        })
        . filter  (Objects::nonNull)
        . collect (Collectors.joining (", "));
        sb.append (parameters);
        
        return sb.toString ();
    }
    
    protected static String getEntityTemplateId (AbsEntity entity) {
        String packageName = AbsEntity.class.getPackage ().getName () + ".";
        String entityName = entity.getClass ().getName ().replace (packageName, "");
        
        StringBuilder sb = new StringBuilder ();
        if (entity instanceof Named) {
            Named named = MiscUtils.cast (entity);
            String name = Utils.clearWhitespaces (named.getName ().toLowerCase ());
            sb.append (name);
        } else {
            sb.append (entityName.toLowerCase ()).append (entity.getId ());
        }
        
        return sb.toString ();
    }
    
}
