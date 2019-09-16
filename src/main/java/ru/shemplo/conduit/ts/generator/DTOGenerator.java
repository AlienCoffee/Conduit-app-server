package ru.shemplo.conduit.ts.generator;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.Algorithms;
import ru.shemplo.snowball.utils.ClasspathManager;
import ru.shemplo.snowball.utils.MiscUtils;

public class DTOGenerator implements Generator {
    
    private final Collector <CharSequence, ?, String> GENERIC_COLLECTOR = Collectors.joining (", ", "<", ">");
    
    @Getter
    private final Map <Class <?>, List <Field>> types = new HashMap <> ();
    
    @Getter
    private final Set <Class <?>> enums = new HashSet <> ();
    
    private final ClasspathManager cpManager;

    public DTOGenerator (ClasspathManager cpManager) {
        this.cpManager = cpManager;
        initialize ();
    }
    
    private void initialize () {
        cpManager.findObjectsWithAnnotation (new HashSet <> (Arrays.asList (DTOType.class))).get (DTOType.class)
        . stream ().filter (obj -> obj instanceof Class).<Class <?>> map (MiscUtils::cast).forEach (t -> {
            if   (t.isEnum ()) { enums.add (t); } 
            else { types.put (t, new ArrayList <> ()); }
        });
    }
    
    @Override
    public void print (PrintWriter pw) {
        pw.println ("import { Enum, EnumType } from \"../../lib/jenum\";");
        pw.println ();
        
        types.keySet ().stream ().sorted (Comparator.comparing (Class::getSimpleName))
             .forEach (type -> printType (type, pw));
        pw.println ();
        
        enums.stream ().sorted (Comparator.comparing (Class::getSimpleName))
             .forEach (type -> printType (type, pw));
    }
    
    private void printType (Class <?> type, PrintWriter pw) {
        final List <String> implTypes = Arrays.asList (type.getGenericInterfaces ()).stream ()
            . filter  (intf -> intf.getClass ().isAnnotationPresent (DTOType.class))
            . map     (this::processType).collect (Collectors.toList ());
        if (type.isEnum ()) {
            String processedType = processType (type);
            pw.println (String.format ("@Enum <%s> ()", processedType));
            pw.println (String.format ("export class %s extends EnumType <%s> () {", 
                processedType, processedType));
            printEnumBody (type, pw);
            pw.println ("}");
        } else {            
            DTOType annotation = type.getAnnotation (DTOType.class);
            Type superType = type.getGenericSuperclass ();
            
            String processedType = processType (type);
            pw.print ("export class ");
            pw.print (processedType);
            
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
            printBody              (type, annotation, pw);
            //printStaticConstructor (type, processedType, pw);
            pw.println ("}");
        }
    }
    
    private void printBody (Class <?> type, DTOType annotation, PrintWriter pw) {
        List <Field> fields = new ArrayList <> ();
        Algorithms.runBFS (type, t -> !Object.class.equals (t), t -> {
            fields.addAll (Arrays.asList (t.getDeclaredFields ()));
            return MiscUtils.cast (Arrays.asList (t.getSuperclass ()));
        });
        
        for (Field field : fields) {
            if (Modifier.isStatic (field.getModifiers ())) {
                continue; // static fields should be declared as custom code
            }
            
            String processedType = processType (field.getGenericType ()), name = field.getName ();
            pw.println (String.format ("    public %s : %s;", name, processedType));
            types.get (type).add (field);
        }
        
        if (annotation.code ().length > 0) {
            for (String code : annotation.code ()) {                
                pw.println (String.format ("    %s", code));
            }
        }
    }
    
    public String processType (Type type) {
        StringBuilder sb = new StringBuilder ();
        
        //System.out.println (type.getClass () + " / " + type);
        if (type instanceof Class) {
            Class <?> ctype = MiscUtils.cast (type);
            String ctypeName = convertName (ctype, false);
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
            String rptypeName = convertName (rptype, false);
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
    
    private void printEnumBody (Class <?> type, PrintWriter pw) {
        if (type.getDeclaredConstructors ().length > 1) {
            String message = "Single constructor is allowed in ENUMs";
            throw new IllegalStateException (message);
        }
        
        Class <Enum <?>> etype = MiscUtils.cast (type);
        String processedType = processType (type);
        
        final Constructor <?> constructor = etype.getDeclaredConstructors () [0];
        List <Pair <Parameter, String>> args = Arrays.asList (constructor.getParameters ()).stream ()
           . map     (p -> Pair.mp (p, processType (p.getParameterizedType ())))
           //. filter  (p -> Character.isLowerCase (p.S.charAt (0)))
           . filter  (p -> !p.F.getName ().startsWith ("$"))
           . collect (Collectors.toList ());
        
        for (var constant : etype.getEnumConstants ()) {
            String cname = constant.name ();
            String arguments = args.stream ().map (p -> getCEPV (etype, constant, p))
                             . collect (Collectors.joining (", "));
            
            pw.println (String.format ("    public static readonly %s = new %s (%s);", 
                cname, processedType, arguments));
        }
        if (etype.getEnumConstants ().length > 0) { pw.println (); }
        
        if (args.size () > 0) {
            String arguments = args.stream ().map (this::getCEPD).collect (Collectors.joining (", "));
            pw.println (String.format ("    private constructor (%s) {", arguments));
            pw.println ("        super ();");
            pw.println ("    }");
        }
    }
    
    @SuppressWarnings ("preview")
    // getConvertedEnumParameterValue
    private String getCEPV (Class <Enum <?>> etype, Enum <?> elem, Pair <Parameter, String> paramAndType) {
        try {
            final Field field = etype.getDeclaredField (paramAndType.F.getName ());
            field.setAccessible (true);
            
            Object value = field.get (elem);
            return switch (paramAndType.S) {
                case "number" -> "" + value;
                case "string" -> "\"" + value + "\"";
                
                default -> "null";
            };
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException 
              | IllegalAccessException e) {
            throw new IllegalStateException (e);
        }
    }
    
    // getConvertedEnumParameterDeclaration
    private String getCEPD (Pair <Parameter, String> paramAndType) {
        String name = paramAndType.F.getName ();
        
        if (Character.isLowerCase (paramAndType.S.charAt (0))) {
            return String.format ("readonly %s : %s", name, paramAndType.S);
        }
        
        return String.format ("readonly %s : any", name);
    }
    
    public static Class <?> getTypeClass (Type type, Map <String, Type> generics) {
        if (type instanceof Class) {
            return MiscUtils.cast (type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType ptype = MiscUtils.cast (type);
            return MiscUtils.cast (ptype.getRawType ());
        } else if (type instanceof TypeVariable) {
            TypeVariable <?> tvar = MiscUtils.cast (type);
            if (generics.containsKey (tvar.getName ())) {
                Type vtype = generics.get (tvar.getName ());
                return getTypeClass (vtype, Collections.emptyMap ());
            }
            
            String message = String.format ("Unknown TypeVariable name: %s", tvar.getName ());
            throw new IllegalArgumentException (message);
        } else if (type != null) {
            System.err.println (type + " / " + type.getClass ());
            String message = String.format ("Unsupported type: %s", 
                type.getClass ().getSimpleName ());
            throw new IllegalArgumentException (message);
        }
        
        return null;
    }
    
    public static Map <String, Type> getGenericTypes (Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = MiscUtils.cast (type);
            Class <?> ctype = MiscUtils.cast (ptype.getRawType ());
            TypeVariable <?> [] typeVariables = ctype.getTypeParameters ();
            Type [] types = ptype.getActualTypeArguments ();
            
            return Stream.iterate (0, i -> i + 1).limit (typeVariables.length)
                 . collect (Collectors.toMap (
                     i -> typeVariables [i].getName (), 
                     i -> types [i])
                 );
        }
        
        return Collections.emptyMap ();
    }
    
    @Getter
    private final List <DTOMappedType> mappedTypes = new ArrayList <> ();
    
    @Getter
    @AllArgsConstructor
    public static class DTOMappedType {
        
        private final Class <?> base;
        
        private boolean inheriting, prototyping;
        
        private String mappedType, prototypeName;
        
        public boolean checkMapping (Class <?> type) {
            return (isInheriting () && getBase ().isAssignableFrom (type)) 
                    || (!isInheriting () && getBase ().equals (type));
        }
        
    }
    
    {
        mappedTypes.add (new DTOMappedType (Boolean.class, false, false, "boolean", null));
        mappedTypes.add (new DTOMappedType (Collection.class, true, true, "Array", "Array"));
        mappedTypes.add (new DTOMappedType (String.class, false, true, "string", "String"));
        mappedTypes.add (new DTOMappedType (Number.class, true, false, "number", "Number"));
        mappedTypes.add (new DTOMappedType (Temporal.class, true, true, "Date", "Date"));
        mappedTypes.add (new DTOMappedType (Pair.class, false, true, "Pair", "Pair"));
        mappedTypes.add (new DTOMappedType (File.class, false, true, "File", "File"));
        mappedTypes.add (new DTOMappedType (Void.class, false, false, "void", null));
        mappedTypes.add (new DTOMappedType (void.class, false, false, "void", null));
        mappedTypes.add (new DTOMappedType (Date.class, true, true, "Date", "Date"));
        mappedTypes.add (new DTOMappedType (Map.class, true, true, "Map", "Map"));
    }
    
    public String convertName (Class <?> type, boolean forPrototype) {
        String pureName = type.getSimpleName ();
        if (type.isAnnotationPresent (DTOType.class) || type.isEnum ()) {
            pureName = pureName.replace ("DTO", "");
        } else if (type.isPrimitive ()) {
            pureName = type.equals (void.class) ? "void" 
                     : type.equals (boolean.class) ? "boolean"
                     : "number";
        } else {
            DTOMappedType mtype = findMappedType (type);
            if (mtype == null) {
                pureName = "Object";
            } else if (forPrototype) {
                pureName = mtype.getPrototypeName ();
            } else {
                pureName = mtype.getMappedType ();
            }
        }
        
        return pureName;
    }
    
    public DTOMappedType findMappedType (Class <?> type) {
        for (DTOMappedType ctype : mappedTypes) {
            if (ctype.checkMapping (type)) {
                return ctype;
            }
        }
        
        return null;
    }
    
}
