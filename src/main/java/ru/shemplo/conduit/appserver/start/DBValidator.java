package ru.shemplo.conduit.appserver.start;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.ServerConstants;
import ru.shemplo.conduit.appserver.entities.AbsEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.repositories.AbsEntityRepository;
import ru.shemplo.snowball.utils.MiscUtils;
import ru.shemplo.snowball.utils.StringManip;

@Component
@RequiredArgsConstructor
public class DBValidator {
    
    private final ConfigurableEnvironment configurableEnvironment;
    private final ApplicationContext applicationContext;
    private final Clock clock;
    
    @Transactional public void validate () throws IOException {
        for (AbsEntity entity : readTemplateFile ()) {
            String repositoryName = entity.getClass ().getSimpleName () + "Repository";
            String packageName = AbsEntityRepository.class.getPackage ().getName ();
            Class <?> repositoryType = null;
            
            try {
                String typeName = String.format ("%s.%s", packageName, repositoryName);
                repositoryType = Class.forName (typeName);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException (cnfe);
            }
            
            final Object tmp  = applicationContext.getBean (repositoryType);
            final AbsEntityRepository <?> repository = MiscUtils.cast (tmp);
            if (!repository.exists (MiscUtils.cast (Example.of (entity)))) {
                repository.save (MiscUtils.cast (entity));
            }
            
            if (entity instanceof UserEntity) {
                UserEntity user = MiscUtils.cast (entity);
                if (UserEntity.getAdminEntity () == null
                    && user.getLogin ().equals ("admin")) {
                    UserEntity.setAdmin (user);
                }
            } else if (entity instanceof PeriodEntity) {
                PeriodEntity period = MiscUtils.cast (entity);
                if (period.getName ().startsWith ("$")
                    && PeriodEntity.getSystem () == null) {
                    PeriodEntity.setSystem (period);
                }
            }
        }
    }
    
    private List <AbsEntity> readTemplateFile () throws IOException {
        final String configFilePathValue = configurableEnvironment
        . getProperty (ServerConstants.DB_TEMPLATE_PROPERTY);
        if (configFilePathValue == null || configFilePathValue.length () == 0) {
            // Templates file not found -- no actions should be done
            return new ArrayList <> ();
        }
        
        final Map <String, AbsEntity> context = new HashMap <> ();
        final List <AbsEntity> sequence = new ArrayList <> ();
        
        final Path configFilePath = Paths.get (configFilePathValue);
        try (
            BufferedReader br = Files.newBufferedReader (configFilePath);
        ) {
            String line = null; int number = 0;
            while ((line = StringManip.fetchNonEmptyLine (br)) != null) {
                sequence.add (buildEntity (number + 1, line, context));
                number += 1;
            }
        }
        
        return sequence;
    }
    
    private AbsEntity buildEntity (int rowNumber, String row, Map <String, AbsEntity> context) {
        final String packageName = AbsEntity.class.getPackage ().getName ();
        final DBTemplateRow template = splitInputString (rowNumber, row);
        
        Class <?> type = null;
        try   { type = Class.forName (packageName + "." + template.getObjectType ()); } 
        catch (ClassNotFoundException cnfe) { throw new IllegalStateException (cnfe); }
        
        if (!AbsEntity.class.isAssignableFrom (type)) {
            String message = String.format ("Type `%s` doesn't extends AbsEntity", 
                                            type.getSimpleName ());
            throw new IllegalStateException (message);
        }
        
        AbsEntity instance = null;
        try   { instance = MiscUtils.cast (type.newInstance ()); } 
        catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException (e);
        }
        
        defineInstanceFields (instance, template, context);
        if (template.getKeyId () != null && template.getKeyId ().trim ().length () > 0) {
            String key = template.getKeyId ().replace ('"', '\0').trim ();
            context.put (key, instance);
        }
        
        return instance;
    }
    
    private AbsEntity defineInstanceFields (AbsEntity entity, DBTemplateRow row, 
            Map <String, AbsEntity> context) {
        final List <Field> fields = new ArrayList <> ();
        
        fields.addAll (Arrays.asList  (entity.getClass ().getDeclaredFields ()));
        if (!Object.class.equals (entity.getClass ().getSuperclass ())) {
            Class <?> superType = entity.getClass ().getSuperclass ();
            List <Field> additional = Arrays.asList (superType.getDeclaredFields ());
            fields.addAll  (additional);
        }
        
        fields.stream ()
        . filter  (f -> !Modifier.isStatic (f.getModifiers ()))
        . filter  (f -> !Modifier.isFinal (f.getModifiers ()))
        . filter  (f -> row.getParams ().containsKey (f.getName ()))
        . peek    (f -> f.setAccessible (true))
        . filter  (f -> {
            try   { return f.get (entity) == null; } 
            catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace ();
                return false; // no access to this field
            }
        })
        . forEach (f -> {
            final String value = row.getParams ().get (f.getName ());
            final Class <?> type = f.getType ();
            
            try   { f.set (entity, convertParameterValue (entity, type, value, context)); } 
            catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException (e);
            }
        });
        
        System.out.println (entity);
        return entity;
    }
    
    private <R> R convertParameterValue (AbsEntity entity, Class <?> required, 
            String value, Map <String, AbsEntity> context) {
        System.out.println ("Converter of `" + required + "` for '" + value + "'");
        
        if (required.isEnum ()) {
            System.out.println ("`" + required + "` is enum type");
            Object result = Enum.valueOf (MiscUtils.cast (required), value);
            return MiscUtils.cast (result);
        } else if (LocalDateTime.class.isAssignableFrom (required)) {
            System.out.println ("`" + required + "` is local date time type");
            if ("{now}".equals (value)) {
                return MiscUtils.cast (LocalDateTime.now (clock));
            } else {
                return MiscUtils.cast (LocalDateTime.parse (value));
            }
        } else if (LocalDate.class.isAssignableFrom (required)) {
            System.out.println ("`" + required + "` is local date type");
            if ("{now}".equals (value)) {
                return MiscUtils.cast (LocalDate.now (clock));
            } else {
                return MiscUtils.cast (LocalDate.parse (value));
            }
        } else if (Boolean.class.isAssignableFrom (required)) {
            System.out.println ("`" + required + "` is boolean type");
            return MiscUtils.cast (Boolean.parseBoolean (value));
        } else if (Number.class.isAssignableFrom (required)) {
            String typeName = required.getSimpleName ();
            if ("Integer".equals (typeName)) {
                typeName = "Int";
            }
            
            try {
                Method method = required.getDeclaredMethod ("parse" + typeName, String.class);
                return MiscUtils.cast (method.invoke (entity, value));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException 
                     | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException (e);
            }
        } else if (String.class.isAssignableFrom (required)) {
            if (value == null || value.length () == 0) {
                return MiscUtils.cast ("");
            }
            
            int left = 0, right = value.length ();
            if (value.startsWith ("\"")) {
                left =  1;
            }
            
            if (value.endsWith ("\"")) {
                right = right - 1;
            }
            
            return MiscUtils.cast (value.substring (left, right));
        } else if (value.startsWith ("#")) {
            value = value.replace ('"', '\0').substring (1).trim ();
            if (!context.containsKey (value)) {
                String message = "Undefined reference `#" + value + "`";
                throw new IllegalStateException (message);
            }
            
            return MiscUtils.cast (context.get (value));
        }
        
        return null;
    }
    
    private static final Pattern DB_TEMPLATE_PARAM_PATTERN;
    private static final Pattern DB_TEMPLATE_KEY_PATTERN;
    
    static {
        final String key = "^([\\w\\d]+)(#\"?([\\w\\d\\s]+)\"?)?:";
        DB_TEMPLATE_KEY_PATTERN = Pattern.compile (key, Pattern.UNICODE_CASE);
        
        final String param = "([\\w\\d]+)=(#?\".*?\"|\\$[\\w\\d]+|\\{[\\w\\d]*\\}|#?[\\w\\d]+|)";
        DB_TEMPLATE_PARAM_PATTERN = Pattern.compile (param, Pattern.UNICODE_CASE);
    }
    
    private final DBTemplateRow splitInputString (int rowNumber, String input) {
        final String collapsedInput = collapseInputString (input);
        
        Matcher matcher = DB_TEMPLATE_KEY_PATTERN.matcher (collapsedInput);
        if (!matcher.find ()) { return new DBTemplateRow (rowNumber); }
        
        DBTemplateRow row = new DBTemplateRow (rowNumber);
        row.setObjectType (matcher.group (1));
        row.setKeyId (matcher.group (3));
        
        matcher = DB_TEMPLATE_PARAM_PATTERN.matcher (collapsedInput);
        while (matcher.find ()) {
            row.getParams ().put (matcher.group (1), matcher.group (2));
        }
        
        System.out.println (row);
        return row;
    }
    
    private final String collapseInputString (String input) {
        StringBuilder sb = new StringBuilder ();
        
        boolean isStringNow = false;
        for (char character : input.toCharArray ()) {
            if (Character.isWhitespace (character) && !isStringNow) {
                continue; // whitespace character that should be skipped
            }
            
            if (character == '"') {isStringNow = !isStringNow;}
            sb.append (character);
        }
        
        return sb.toString ();
    }
    
}
