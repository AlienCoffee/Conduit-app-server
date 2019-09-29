package ru.shemplo.conduit.ts.generator;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.stuctures.Trio;
import ru.shemplo.snowball.utils.ClasspathManager;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
public class APIGenerator implements Generator {
    
    private final Set <Method> methods = new LinkedHashSet <> ();
    private final AtomicInteger counter = new AtomicInteger ();
    
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
    
    @Override
    public void print (PrintWriter pw) {
        String dtos = Stream.concat (dtoGenerator.getTypes ().keySet ().stream (), 
                                     dtoGenerator.getEnums ().stream ())
                    . map (type -> dtoGenerator.convertName (type, false))
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
        pw.println ("    });");
        pw.println ("    return map;");
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
        //processTypesAssignment (rType, "", "answer", 0, pw);
        initializeObject ("answer", "answer", rType, "", 0, pw);
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
        
        getStreamOfParameters (method).map (param -> {
            boolean required = true;
            if (param.F.isAnnotationPresent (RequestParam.class) && required) {
                RequestParam rp = param.F.getAnnotation (RequestParam.class);
                required = rp.required ();
            }
            
            return Pair.mp (param, required);
        }).sorted ((a, b) -> (b.S ? 1 : 0) - (a.S ? 1 : 0)).forEach (pair -> {
            params.add (String.format ("%s%s : %s", pair.F.S, pair.S ? "" : "?", pair.F.T));
        });
        
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
    
    ////////////////////////////////////////////////////////////////////////////////////////
    
    private void initializeObject (String name, String source, Type type, String offset, int level, PrintWriter pw) {
        Map <String, Type> gens = DTOGenerator.getGenericTypes (type);
        //System.out.println (type + " / " + gens);
        Class <?> ctype = DTOGenerator.getTypeClass (type, gens);
        if (dtoGenerator.getTypes ().containsKey (ctype) && true) {
            dtoGenerator.getTypes ().get (ctype).forEach (field -> {
                String srcName = (source != null ? source + "." : "") + field.getName ();
                String varName = name + "." + field.getName ();
                Type ftype = field.getGenericType ();
                
                initializeVariable (varName, srcName, false, ftype, gens, offset, 0, pw);
            });
        } else if (Collection.class.isAssignableFrom (ctype)) {
            ParameterizedType ptype = MiscUtils.cast (type);
            
            String varIName = "i" + counter.getAndIncrement ();
            pw.println (String.format ("            %sfor (let %s in %s) {", offset, varIName, source));
            
            Type vtype = ptype.getActualTypeArguments () [0];
            String varOName = "tmpObj" + counter.get (), varSName = "tmpSrc" + counter.getAndIncrement ();
            String osource = String.format ("%s [%s]", source, varIName);
            initializeVariable (varSName, osource, true, null, gens, offset + "    ", level + 1, pw);
            String processedType = dtoGenerator.processType (vtype);
            if (Character.isLowerCase (processedType.charAt (0))) {
                initializeVariable (varOName, varSName, true, vtype, gens, offset + "    ", level + 1, pw);
            } else {
                String initedValue = String.format ("new %s ()", processedType);
                initializeVariable (varOName, initedValue, true, null, gens, offset + "    ", level + 1, pw);
                initializeObject (varOName, varSName, vtype, offset + "    ", level + 1, pw);
            }
            pw.println (String.format ("                %s%s.push (%s);", offset, name, varOName));
            pw.println (String.format ("            %s}", offset));
        } else if (Map.class.isAssignableFrom (ctype)) {
            ParameterizedType ptype = MiscUtils.cast (type);
            
            String varKName = "key" + counter.get ();
            pw.println (String.format ("            %sObject.keys (%s).forEach (%s => {", offset, source, varKName));
            Type ktype = ptype.getActualTypeArguments () [0];
            initializeVariable (varKName + "I", varKName, true, ktype, gens, offset + "    ", level + 1, pw);
            
            Type vtype = ptype.getActualTypeArguments () [1];
            String varVName= "val" + counter.getAndIncrement ();
            String vsource = String.format ("%s [%s]", source, varKName);
            initializeVariable (varVName + "I", vsource, true, vtype, gens, offset + "    ", level + 1, pw);
            pw.println (String.format ("                %s%s.set (%s, %s);", offset, name, varKName + "I", varVName + "I"));
            pw.println (String.format ("            %s});", offset));
        } else if (ctype.isArray ()) {
            log.warn ("Initializing of array in API generator is not implemented (use List extend)");
            pw.println ("// not implemented: array");
        } else if (ctype.isEnum ()) {
            pw.println ("// not implemented: enum");
        } else {
            String keyName = "key" + counter.getAndIncrement ();
            pw.println (String.format ("            %sObject.keys (%s).forEach (%s => {", 
                offset, source, keyName));
            pw.println (String.format ("                %s%s [%s] = %s [%s];", offset, 
                name, keyName, source, keyName));
            pw.println (String.format ("            %s});", offset));
        }
    }
    
    private void initializeVariable (String name, String source, boolean declare, Type type, 
            Map <String, Type> generics, String offset, int level, PrintWriter pw) {
        //System.out.println ("Init' varible: " + name);
        Class <?> ctype = DTOGenerator.getTypeClass (type, generics);
        if (type == null && source != null && source.length () > 0) {
            String prefix = declare ? "let " : "";
            pw.println (String.format ("            %s%s%s = %s; // custom type", 
                offset, prefix, name, source));
        } else if (dtoGenerator.getTypes ().containsKey (ctype)) {
            String varOName = "tmpObj" + counter.getAndIncrement ();
            String processedType = dtoGenerator.processType (type);
            String initedValue = String.format ("new %s ()", processedType);
            initializeVariable (varOName, initedValue, true, null, null, offset, level, pw);
            initializeObject (varOName, source, type, offset, level, pw);
            initializeVariable (name, varOName, false, null, null, offset, level, pw);
        } else if (Collection.class.isAssignableFrom (ctype)) {
            ParameterizedType ptype = null;
            if (type instanceof ParameterizedType) {
                ptype = MiscUtils.cast (type);
            } else if (type instanceof TypeVariable) {
                TypeVariable <?> tvar = MiscUtils.cast (type);
                if (generics.containsKey (tvar.getName ())) {
                    ptype = MiscUtils.cast (generics.get (tvar.getName ()));
                }
            } else {
                System.out.println (":( collection");
            }
            
            String varName = "tmpArray" + counter.getAndIncrement ();
            initializeVariable (varName, "[]", true, null, null, offset, level, pw);
            initializeObject (varName, source, ptype, offset, level, pw);
            initializeVariable (name, varName, declare, null, null, offset, level, pw);
        } else if (String.class.isAssignableFrom (ctype)) {
            String prefix = declare ? "let " : "";
            pw.println (String.format ("            %s%s%s = \"\" + %s; // string", offset, prefix, name, source));
        } else if (Number.class.isAssignableFrom (ctype) || ctype.isPrimitive ()) {
            String prefix = declare ? "let " : "";
            String postfix = boolean.class.isAssignableFrom (ctype) ? source
                           : void.class.isAssignableFrom (ctype) ? "null" 
                           : "+" + source;
            pw.println (String.format ("            %s%s%s = %s; // number || primitive", 
                offset, prefix, name, postfix));
        } else if (Map.class.isAssignableFrom (ctype)) {
            ParameterizedType ptype = null;
            if (type instanceof ParameterizedType) {
                ptype = MiscUtils.cast (type);
            } else if (type instanceof TypeVariable) {
                TypeVariable <?> tvar = MiscUtils.cast (type);
                if (generics.containsKey (tvar.getName ())) {
                    ptype = MiscUtils.cast (generics.get (tvar.getName ()));
                }
            } else {
                System.out.println (":( map");
            }
            
            String varName = "tmpMap" + counter.getAndIncrement ();
            initializeVariable (varName, "new Map ()", true, null, null, offset, level, pw);
            initializeObject (varName, source, ptype, offset, level, pw);
            initializeVariable (name, varName, false, null, null, offset, level, pw);
        } else if (LocalDate.class.isAssignableFrom (ctype) || Date.class.isAssignableFrom (ctype)
                || LocalDateTime.class.isAssignableFrom (ctype)) {
            String prefix = declare ? "let " : "";
            pw.println (String.format ("            %s%s%s = new Date (%s); // date", 
                offset, prefix, name, source));
        } else if (ctype.isArray ()) {
            pw.println ("// not implemented: array (variable) / " + name + " / " + source);
        } else if (ctype.isEnum ()) {
            String convertedType = dtoGenerator.convertName (ctype, true);
            String initedValue = String.format ("%s.valueByName (\"\" + %s)", convertedType, source);
            initializeVariable (name, initedValue, false, null, null, offset, level, pw);
        } else if (Object.class.isAssignableFrom (ctype)) {
            String prefix = declare ? "let " : "";
            pw.println (String.format ("            %s%s%s = %s; // object", offset, prefix, name, source));
        }
    }
    
}
