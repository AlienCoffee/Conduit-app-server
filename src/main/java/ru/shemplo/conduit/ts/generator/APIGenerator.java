package ru.shemplo.conduit.ts.generator;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.*;

import ru.shemplo.conduit.ts.generator.DTOGenerator.DTOMappedType;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.stuctures.Trio;
import ru.shemplo.snowball.utils.ClasspathManager;
import ru.shemplo.snowball.utils.MiscUtils;

public class APIGenerator {
    
    private final Set <Method> methods = new LinkedHashSet <> ();
    
    private final ClasspathManager cpManager;
    private final DTOGenerator dtoGenerator;
    
    public APIGenerator (ClasspathManager cpManager, DTOGenerator dtoGenerator) {
        this.dtoGenerator = dtoGenerator;
        this.cpManager = cpManager;
        initialize ();
    }
    
    private void initialize () {
        final Map <Class <? extends Annotation>, List <Object>> objects = cpManager
        . findObjectsWithAnnotation (new HashSet <> (Arrays.asList (
            Mapping.class, GetMapping.class, PostMapping.class, DeleteMapping.class
        )));
            
        objects.values ().stream ().flatMap (List::stream)
        . filter (obj -> obj instanceof Method).distinct ()
        . <Method> map (MiscUtils::cast)
        . filter (method -> method.getDeclaringClass ().isAnnotationPresent (RestController.class))
        . forEach (methods::add);
    }
    
    public void print (PrintWriter pw) {
        String dtos = dtoGenerator.getTypes ().stream ().map (type -> dtoGenerator.convertName (type, false))
                    . collect (Collectors.joining (",\n\t"));
        pw.println (String.format ("import {\n\t%s\n} from \"./gen-dtos\";", dtos));
        pw.println ("import { Pair, assignType } from \"../common\";");
        //pw.println ("import { ResponseBox } from \"./gen-dtos\";");
        pw.println ("import { sendRequest } from \"../network\";");
        pw.println ();
        
        pw.println ("function convertObj2Map (obj : any) {");
        pw.println ("    var map = new Map ();");
        pw.println ("    Object.keys (obj).forEach (key => {");
        pw.println ("        map.set (key, obj [key]);");
        pw.println ("    }");
        pw.println ("}");
        pw.println ();
        
        final Map <Class <?>, List <Method>> services = methods.stream ()
            . sorted  (Comparator.comparing (Method::getName))
            . collect (Collectors.groupingBy (Method::getDeclaringClass));
        services.entrySet ().stream ().map (Pair::fromMapEntry)
        . sorted  (Comparator.comparing (snm -> snm.F.getSimpleName ()))
        . forEach (snm -> printService (snm.F, snm.S, pw));
    }

    private void printService (Class <?> type, List <Method> methods, PrintWriter pw) {
        pw.print   ("export class ");
        pw.print   (type.getSimpleName ());
        pw.println (" {");
        for (Method method : methods) {
            printMethod (method, pw);
        }
        pw.println ("}");
    }
    
    private void printMethod (Method method, PrintWriter pw) {
        pw.print ("    public static async ");
        String methodName = method.getName ().replace ("handle", "");
        methodName = Character.toLowerCase (methodName.charAt (0)) 
                   + methodName.substring (1);
        String methodParameters = null;
        pw.print   (methodName);
        pw.print   (" (");
        pw.print   (methodParameters = prepareMethodArguments (method));
        pw.print   (") : ");
        final Type rType = method.getGenericReturnType ();
        String rgType = dtoGenerator.processType (rType);
        pw.print   (String.format ("Promise <%s>", rgType));
        pw.println (" {");
        Trio <String, String, String> methodAndURLAndParams = prepareMethodAndURLAndParams (method);
        boolean isInline = Arrays.asList ("GET", "DELETE").indexOf (methodAndURLAndParams.F) != -1;
        if (!isInline) {
            pw.println ("        var formData = new FormData ();");
            getStreamOfParameters (method).forEach (param -> {
                final String stub = prepareParameterStub (param.F, param.S, param.T);
                pw.println (String.format ("        formData.append (\"%s\", %s);", param.S, stub));
            });
        }
        pw.print   ("        var answer = await sendRequest <");
        pw.print   (rgType);
        pw.print   ("> (\"");
        pw.print   (methodAndURLAndParams.F);
        pw.print   ("\", \"");
        pw.print   (methodAndURLAndParams.S);
        if (isInline && methodParameters.length () > 0) {
            pw.print ("?");
            pw.print (methodAndURLAndParams.T);
        }
        pw.print   ("\", ");
        pw.print   (isInline ? "null" : "formData");
        pw.println (");");
        pw.println ("        if (answer && !answer.error) {");
        processTypesAssignment (rType, "", "answer", 0, pw);
        pw.println ("        }");
        pw.println ("        return answer;");
        pw.println ("    }");
    }
    
    private Stream <Trio <Parameter, String, String>> getStreamOfParameters (Method method) {
        return Arrays.asList (method.getParameters ()).stream ()
             .filter (param -> param.isAnnotationPresent (RequestParam.class))
             . map   (param -> {
                 final RequestParam paramAnnot = param.getAnnotation (RequestParam.class);
                 final Type type = param.getParameterizedType ();
                 
                 String name = paramAnnot.value  ().length () > 0 
                             ? paramAnnot.value  () 
                             : param.getName ();
                 
                 String ptype = dtoGenerator.processType (type);
                 return Trio.mt (param, name, ptype);
             });
    }
    
    private String prepareMethodArguments (Method method) {
        List <String> params = new ArrayList <> ();
        
        getStreamOfParameters (method).forEach (param -> params.add (param.S + " : " + param.T));
        
        return params.stream ().collect (Collectors.joining (", "));
    }
    
    private Trio <String, String, String> prepareMethodAndURLAndParams (Method method) {
        String sendMethod = null, url = null, params = null;
        if (method.isAnnotationPresent (GetMapping.class)) {
            url = method.getAnnotation (GetMapping.class).value () [0];
            params = prepareParametersString (method);
            sendMethod = "GET";
        } else if (method.isAnnotationPresent (PostMapping.class)) {
            url = method.getAnnotation (PostMapping.class).value () [0];
            sendMethod = "POST";
        } else if (method.isAnnotationPresent (DeleteMapping.class)) {
            url = method.getAnnotation (DeleteMapping.class).value () [0];
            params = prepareParametersString (method);
            sendMethod = "DELETE";
        }
        
        return Trio.mt (sendMethod, url, params);
    }
    
    private String prepareParametersString (Method method) {
        StringJoiner sj = new StringJoiner ("&");
        
        getStreamOfParameters (method).forEach (param -> {
            final String stub = prepareParameterStub (param.F, param.S, param.T);
            sj.add (String.format ("%s=\"+(%s)+\"", param.S, stub));
        });
        
        return sj.toString ();
    }
    
    private String prepareParameterStub (Parameter param, String name, String mappedType) {
        if (mappedType.equals ("number")) {
            return String.format ("(%s || %s == 0) ? '' + %s : ''", name, name, name);
        } else if (mappedType.equals ("string")) {
            return String.format ("%s ? '' + %s : ''", name, name);
        } else if (mappedType.equals ("boolean")) {
            return String.format ("%s !== null ? '' + %s : ''", name, name);
        } else if (param.getParameterizedType () instanceof Class) {
            Class <?> ctype = MiscUtils.cast (param.getParameterizedType ());
            if (ctype.isEnum ()) {
                return String.format ("%s ? '' + %s.name : ''", name, name);
            }
        }
        
        return String.format ("JSON.stringify (%s)", name);
    }
    
    private void processTypesAssignment (Type type, String offset, String address, int level, PrintWriter pw) {
        if (type instanceof Class) {
            Class <?> ctype = MiscUtils.cast (type);
            processRawTypeAssignment (ctype, offset, address, pw);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType ptype = MiscUtils.cast (type);
            Class <?> ctype = MiscUtils.cast (ptype.getRawType ());
            processRawTypeAssignment (ctype, offset, address, pw);
            
            processBodyOfAssigningClass (ctype, ptype, ptype.getActualTypeArguments (), 
                    offset, address, level, pw);
        }
    }
    
    private void processBodyOfAssigningClass (Class <?> ctype, Type type, Type [] generics, 
            String offset, String address, int level, PrintWriter pw) {
        if (ctype != null && ctype.isAnnotationPresent (DTOType.class)) {
           //DTOType annotation = ctype.getAnnotation (DTOType.class);
            for (Field field : ctype.getDeclaredFields ()) {
                String fieldAddress = String.join (".", address, field.getName ());
                Type fieldType = field.getGenericType ();
                if (fieldType instanceof TypeVariable) {
                    TypeVariable <?> typeVar = MiscUtils.cast (fieldType);
                    int index = Arrays.asList (ctype.getTypeParameters ()).indexOf (typeVar);
                    fieldType = generics [index];
                }
                processTypesAssignment (fieldType, offset, fieldAddress, level, pw);
            }
        } else if (Collection.class.isAssignableFrom (ctype) || ctype.isArray ()) {
            processCollectionInAssigning (type, address, offset, level, pw);
        } else if (Map.class.isAssignableFrom (ctype)) {
            pw.println (String.format ("            %s%s = convertObj2Map (%s);", offset, address, address));
            processMapInAssigning (type, address, offset, level, pw);
        }
    }
    
    private void processCollectionInAssigning (Type type, String addres, String offset, int level, PrintWriter pw) {
        pw.println (String.format ("            %sfor (let i%d of %s) {", offset, level, addres));
        if (type instanceof Class) {
            final Class <?> ctype = MiscUtils.cast (type);
            Class <?> atype = ctype.getComponentType ();
            if (atype.isArray ()) {
                processCollectionInAssigning (atype, "i" + level, offset + "    ", level + 1, pw);
            } else {
                final String mappedType = dtoGenerator.convertName (atype, true);
                printTypeAssignment (offset + "    ", "i" + level, mappedType, pw);
            }
        } if (type instanceof ParameterizedType) { // Collection <T>
            final ParameterizedType ptype = MiscUtils.cast (type);
            Type atype = ptype.getActualTypeArguments () [0]; // T
            
            if (atype instanceof Class) {
                final Class <?> ctype = MiscUtils.cast (atype);
                if (ctype.isArray ()) {
                    processCollectionInAssigning (ctype, "i" + level, offset + "    ", level + 1, pw);
                } else {
                    final String mappedType = dtoGenerator.convertName (ctype, true);
                    printTypeAssignment (offset + "    ", "i" + level, mappedType, pw);
                }
            } else if (atype instanceof ParameterizedType) {
                final ParameterizedType pptype = MiscUtils.cast (atype);
                Class <?> rtype = MiscUtils.cast (pptype.getRawType ());
                if (rtype.isArray () || Collection.class.isAssignableFrom (rtype)) {
                    processCollectionInAssigning (rtype, "i" + level, offset + "    ", level + 1, pw);
                } else {
                    final String mappedType = dtoGenerator.convertName (rtype, false);
                    printTypeAssignment (offset + "    ", "i" + level, mappedType, pw);
                }
            }
        }
        pw.println (String.format ("            %s}", offset));
    }
    
    private void processMapInAssigning (Type type, String addres, String offset, int level, PrintWriter pw) {
        pw.println (String.format ("            %sfor (let i%d of %s.keys ()) {", offset, level, addres));
        ParameterizedType ptype = MiscUtils.cast (type);
        Type ktype = ptype.getActualTypeArguments () [0];
        if (ktype instanceof Class) {
            Class <?> cktype = MiscUtils.cast (ktype);
            processTypesAssignment (cktype, offset + "    ", "i" + level, level + 1, pw);
        } else if (ktype instanceof ParameterizedType) {
            ParameterizedType pktype = MiscUtils.cast (ktype);
            Class <?> rpktype = MiscUtils.cast (pktype.getRawType ());
            processBodyOfAssigningClass (rpktype, pktype, pktype.getActualTypeArguments (), 
                    offset + "     ", "i" + level, level + 1, pw);
        }
        
        String valueAddress = String.format ("%s.get (i%d)", addres, level);
        Type vtype = ptype.getActualTypeArguments () [1];
        if (vtype instanceof Class) {
            Class <?> cktype = MiscUtils.cast (vtype);
            String mappedType = dtoGenerator.convertName (cktype, true);
            printTypeAssignment (offset + "    ", valueAddress, mappedType, pw);
            processTypesAssignment (cktype, offset + "    ", valueAddress, level + 1, pw);
        } else if (vtype instanceof ParameterizedType) {
            ParameterizedType pktype = MiscUtils.cast (vtype);
            Class <?> rpktype = MiscUtils.cast (pktype.getRawType ());
            String mappedType = dtoGenerator.convertName (rpktype, true);
            printTypeAssignment (offset + "    ", valueAddress, mappedType, pw);
            processBodyOfAssigningClass (rpktype, pktype, pktype.getActualTypeArguments (), 
                    offset + "    ", valueAddress, level + 1, pw);
        }
        pw.println (String.format ("            %s}", offset));
    }
    
    private void processRawTypeAssignment (Class <?> type, String offset, String address, PrintWriter pw) {
        if (type.isAnnotationPresent (DTOType.class)) {
            DTOType annotation = type.getAnnotation (DTOType.class);
            if (annotation.generateTypeAssignment ()) {
                printTypeAssignment ("", address, dtoGenerator.convertName (type, true), pw);
            }
        } else {
            dtoGenerator.getMappedTypes ().stream ().filter (mtype -> mtype.checkMapping (type))
            . filter (DTOMappedType::isPrototyping).findFirst ().ifPresent (mtype -> {
                printTypeAssignment (offset, address, mtype.getPrototypeName (), pw);
            });
        }
    }
    
    private void printTypeAssignment (String offset, String address, String mappedType, PrintWriter pw) {
        pw.println (String.format ("            %sassignType (%s, %s.prototype);", offset, address, mappedType));
    }
    
}
