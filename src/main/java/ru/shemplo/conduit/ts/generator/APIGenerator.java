package ru.shemplo.conduit.ts.generator;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.*;

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
        pw.println ("import { Pair, assignType } from \"../common\";");
        pw.println ("import { ResponseBox } from \"./gen-dtos\";");
        pw.println ("import { sendRequest } from \"../network\";");
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
        processTypesAssignment (rType, "", pw);
        if (!rgType.equals ("void")) {   
            pw.println ("        return answer;");
        }
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
    
    private void processTypesAssignment (Type type, String address, PrintWriter pw) {
        
    }
    
}
