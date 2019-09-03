package ru.shemplo.conduit.ts.generator;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.stuctures.Trio;
import ru.shemplo.snowball.utils.ClasspathManager;
import ru.shemplo.snowball.utils.MiscUtils;

public class DTOGenerator {
    
    private final Collector <CharSequence, ?, String> GENERIC_COLLECTOR = Collectors.joining (", ", "<", ">");
    
    private final Set <Class <?>> types = new LinkedHashSet <> ();
    
    private final ClasspathManager cpManager;

    public DTOGenerator (ClasspathManager cpManager) {
        this.cpManager = cpManager;
        initialize ();
    }
    
    private void initialize () {
        cpManager.findObjectsWithAnnotation (new HashSet <> (Arrays.asList (DTOType.class)))
        . get (DTOType.class).stream ().filter (obj -> obj instanceof Class)
        . <Class <?>> map (MiscUtils::cast).forEach (types::add);
    }
    
    public void print (PrintWriter pw) {
        types.stream ().sorted (Comparator.comparing (Class::getSimpleName))
             .forEach (type -> printType (type, pw));
    }
    
    private void printType (Class <?> type, PrintWriter pw) {
        final List <String> implTypes = Arrays.asList (type.getGenericInterfaces ()).stream ()
            . filter  (intf -> intf.getClass ().isAnnotationPresent (DTOType.class))
            . map     (this::processType).collect (Collectors.toList ());
        DTOType annotation = type.getAnnotation (DTOType.class);
        Type superType = type.getGenericSuperclass ();
        
        pw.print ("export class ");
        pw.print (processType (type));
        
        if (annotation.superclass () != null && annotation.superclass ().length () > 0) {
            pw.print (" extends "); pw.print (annotation.superclass ());
        } else if (superType.getClass ().isAnnotationPresent (DTOType.class)) {
            pw.print (" extends "); pw.print (processType (superType));
        }
        
        implTypes.addAll (Arrays.asList (annotation.interfaces ()));
        if (implTypes.size () > 0) {
            pw.print (" implements ");
            pw.print (implTypes.stream ().collect (Collectors.joining (", ")));
        }
        
        pw.println (" {");
        printBody (type, pw);
        pw.println ("}");
    }
    
    private void printBody (Class <?> type, PrintWriter pw) {
        for (Field field : type.getDeclaredFields ()) {
            if (Modifier.isStatic (field.getModifiers ())) {
                continue; // static fields should be declared as custom code
            }
            
            pw.print ("    public ");
            pw.print (field.getName ());
            pw.print (" : ");
            pw.print (processType (field.getGenericType ()));
            pw.println (";");
        }
    }
    
    public String processType (Type type) {
        StringBuilder sb = new StringBuilder ();
        
        //System.out.println (type.getClass () + " / " + type);
        if (type instanceof Class) {
            Class <?> ctype = MiscUtils.cast (type);
            String ctypeName = convertName (ctype);
            sb.append (ctypeName);
            
            List <String> typeParameters = Arrays.asList (ctype.getTypeParameters ()).stream ()
                    . map (TypeVariable::getTypeName)
                    . collect (Collectors.toList ());
            if (typeParameters.size () > 0) {
                sb.append (" ").append (
                    typeParameters.stream ().collect (GENERIC_COLLECTOR)
                );
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType ptype = MiscUtils.cast (type);
            
            Class <?> rptype = MiscUtils.cast (ptype.getRawType ());
            String rptypeName = convertName (rptype);
            sb.append (rptypeName);
            
            if (!rptypeName.equals ("any")) {
                String params = Arrays.asList (ptype.getActualTypeArguments ()).stream ()
                        . map (this::processType).collect (GENERIC_COLLECTOR);
                sb.append (" ").append (params);
            }
            
        } else if (type instanceof TypeVariable) {
            TypeVariable <?> vtype = MiscUtils.cast (type);
            sb.append (vtype.getName ());
        }
        
        return sb.toString ();
    }
    
    private final List <Trio <Class <?>, String, Boolean>> mappedTypes = new ArrayList <> ();
    
    {
        mappedTypes.add (Trio.mt (Boolean.class, "boolean", false));
        mappedTypes.add (Trio.mt (String.class, "string", false));
        mappedTypes.add (Trio.mt (Number.class, "number", true));
        mappedTypes.add (Trio.mt (List.class, "Array", true));
        mappedTypes.add (Trio.mt (Pair.class, "Pair", false));
        mappedTypes.add (Trio.mt (File.class, "File", false));
        mappedTypes.add (Trio.mt (Void.class, "void", false));
        mappedTypes.add (Trio.mt (Map.class, "Map", true));
    }
    
    public String convertName (Class <?> type) {
        String pureName = type.getSimpleName ();
        if (type.isAnnotationPresent (DTOType.class)) {
            pureName = pureName.replace ("DTO", "");
        } else if (type.isPrimitive ()) {
            pureName = "number";
        } else {
            boolean found = false;
            for (Trio <Class <?>, String, Boolean> ctype : mappedTypes) {
                if ((ctype.T && ctype.F.isAssignableFrom (type)) 
                        || (!ctype.T && ctype.F.equals (type))) {
                    pureName = ctype.S;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                pureName = "any";
            }
        }
        
        return pureName;
    }
    
}
