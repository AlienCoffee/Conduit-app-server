package ru.shemplo.conduit.appserver.start;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Component
@RequiredArgsConstructor
public class MethodsScanner {
    
    private final Map <String, Method> methods = new LinkedHashMap <> ();
    private boolean getMethodAdded = false;
    
    private final AccessGuard accessGuard;
    
    public final void scanMethods (ListableBeanFactory context) {
        context.getBeansWithAnnotation (Service.class).values ().stream ()
        . map     (this::fetchClass).map (this::fetchMethods)
        . forEach (methods::putAll);
        
        final Method method = MiscUtils.getMethod ();
        methods.put (method.getName (), method);
    }
    
    private Class <?> fetchClass (Object object) {
        Class <?> type = object.getClass ();
        while (!type.isAnnotationPresent (Service.class)
                && !type.equals (Object.class)) {
            type = type.getSuperclass ();
        }
        
        return type;
    }
    
    private Map <String, Method> fetchMethods (Class <?> type) {
        return Arrays.asList (type.getDeclaredMethods ()).stream ()
        . filter  (method -> method.isAnnotationPresent (ProtectedMethod.class))
        . filter  (method -> !Modifier.isStatic (method.getModifiers ()))
        . filter  (method -> Modifier.isPublic (method.getModifiers ()))
        . collect (Collectors.toMap (Method::getName, __ -> __));
    }
    
    public final Map <String, Method> getProtectedMethods () {
        if (!getMethodAdded) {
            final Method method = MiscUtils.getMethod ();
            methods.put (method.getName (), method);
            getMethodAdded = true;
        }
        
        accessGuard.method  (MiscUtils.getMethod ());
        return Collections.unmodifiableMap (methods);
    }
    
    public final Method getMethodByName (String methodName) throws EntityNotFoundException {
        if (!methods.containsKey (methodName)) {
            throw new EntityNotFoundException ("No such protected method");
        }
        
        return methods.get (methodName);
    }
    
}
