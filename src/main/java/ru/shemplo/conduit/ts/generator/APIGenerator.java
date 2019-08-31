package ru.shemplo.conduit.ts.generator;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import ru.shemplo.snowball.stuctures.Pair;
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
        pw.println ("import { ResponseBox } from \"./gen-dtos\";");
        pw.println ("import { sendRequest } from \"../network\";");
        pw.println ("import { Pair } from \"../common\";");
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
        pw.print ("    public static ");
        String methodName = method.getName ().replace ("handle", "");
        methodName = Character.toLowerCase (methodName.charAt (0)) 
                   + methodName.substring (1);
        pw.print   (methodName);
        pw.print   (" (");
        pw.print   (prepareMethodArguments (method));
        pw.print   (") : ");
        final Type rType = method.getGenericReturnType ();
        String rgType = dtoGenerator.processType (rType);
        pw.print   (String.format ("Promise <%s>", rgType));
        pw.println (" {");
        Pair <String, String> methodAndURL = prepareMethodAndURL (method);
        if (!"GET".equals (methodAndURL.F)) {
            pw.println ("        var formData = new FormData ();");
        }
        if (!rgType.equals ("void")) {   
            pw.print ("        return ");
        } else {
            pw.print ("        ");
        }
        pw.print   ("sendRequest (\"");
        pw.print   (methodAndURL.F);
        pw.print   ("\", \"");
        pw.print   (methodAndURL.S);
        pw.print   ("\", ");
        pw.print   ("GET".equals (methodAndURL.F) ? "null" : "formData");
        pw.println (");");
        pw.println ("    }");
    }
    
    private String prepareMethodArguments (Method method) {
        List <String> params = new ArrayList <> ();
        
        for (Parameter parameter : method.getParameters ()) {
            if (parameter.isAnnotationPresent (RequestParam.class)) {
                final RequestParam paramAnnot = parameter.getAnnotation (RequestParam.class);
                final Type paramType = parameter.getParameterizedType ();
                
                String paramName = paramAnnot.value  ().length () > 0 
                                 ? paramAnnot.value  () 
                                 : parameter.getName ();
                
                String processedType = dtoGenerator.processType (paramType);
                params.add (paramName + " : " + processedType);
            }
        }
        
        return params.stream ().collect (Collectors.joining (", "));
    }
    
    private Pair <String, String> prepareMethodAndURL (Method method) {
        String sendMethod = null, url = null;
        if (method.isAnnotationPresent (GetMapping.class)) {
            url = method.getAnnotation (GetMapping.class).value () [0];
            sendMethod = "GET";
        } else if (method.isAnnotationPresent (PostMapping.class)) {
            url = method.getAnnotation (PostMapping.class).value () [0];
            sendMethod = "POST";
        } else if (method.isAnnotationPresent (DeleteMapping.class)) {
            url = method.getAnnotation (DeleteMapping.class).value () [0];
            sendMethod = "DELETE";
        }
        
        return Pair.mp (sendMethod, url);
    }
    
}
