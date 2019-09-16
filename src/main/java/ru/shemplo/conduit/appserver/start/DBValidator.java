package ru.shemplo.conduit.appserver.start;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.ServerConstants;
import ru.shemplo.conduit.appserver.entities.AbsEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.repositories.AbsEntityRepository;
import ru.shemplo.snowball.utils.MiscUtils;
import ru.shemplo.snowball.utils.StringManip;

@Slf4j
@Component
@RequiredArgsConstructor
public class DBValidator {
    
    private final ConfigurableEnvironment configurableEnvironment;
    private final ApplicationContext applicationContext;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    
    @Transactional public void validate () throws IOException {
        final long start = System.currentTimeMillis ();
        
        final Map <String, AbsEntity> context = new HashMap <> ();
        int rows = 0;
        
        for (DBTemplateRow template : readTemplateFile ()) {
            AbsEntity entity = buildEntity (template, context, false);
            
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
            Example <AbsEntity> example = Example.of (entity);
            
            if (repository.exists (MiscUtils.cast (example))) {
                entity = repository.findOne (MiscUtils.cast (example)).get ();
            }
            
            defineInstanceFields (entity, template, context, true);
            entity = repository.save (MiscUtils.cast (entity));
            context.put ("" + template.getRow (), entity);
            
            if (template.getKeyId () != null && template.getKeyId ().trim ().length () > 0) {
                String key = template.getKeyId ().replace ('"', '\0').trim ();
                context.put (key, entity);
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
            
            rows += 1;
        }
        
        long end = System.currentTimeMillis ();
        log.info (String.format ("Database validation done (rows: %d, time: %dms)", rows, end - start));
    }
    
    private List <DBTemplateRow> readTemplateFile () throws IOException {
        final String configFilePathValue = configurableEnvironment
        . getProperty (ServerConstants.DB_TEMPLATE_PROPERTY);
        if (configFilePathValue == null || configFilePathValue.length () == 0) {
            // Templates file not found -- no actions should be done
            return new ArrayList <> ();
        }
        
        final List <DBTemplateRow> sequence = new ArrayList <> ();
        
        final Path configFilePath = Paths.get (configFilePathValue);
        try (
            BufferedReader br = Files.newBufferedReader (configFilePath);
        ) {
            String line = null; int number = 0;
            while ((line = StringManip.fetchNonEmptyLine (br)) != null) {
                if (line.startsWith ("//")) { continue; } // it's comment
                sequence.add (splitInputString (number, line));
                number += 1;
            }
        }
        
        return sequence;
    }
    
    private AbsEntity buildEntity (DBTemplateRow template, Map <String, AbsEntity> context, boolean full) {
        final String packageName = AbsEntity.class.getPackage ().getName ();
        
        Class <?> type = null;
        try   { type = Class.forName (packageName + "." + template.getObjectType ()); } 
        catch (ClassNotFoundException cnfe) { throw new IllegalStateException (cnfe); }
        
        if (!AbsEntity.class.isAssignableFrom (type)) {
            String message = String.format ("Type `%s` doesn't extends AbsEntity", 
                                            type.getSimpleName ());
            throw new IllegalStateException (message);
        }
        
        AbsEntity instance = null;
        try   { instance = MiscUtils.cast (type.getDeclaredConstructor ().newInstance ()); } 
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException (e);
        }
        
        instance = defineInstanceFields (instance, template, context, full);
        context.put ("" + template.getRow (), instance);
                
        if (template.getKeyId () != null && template.getKeyId ().trim ().length () > 0) {
            String key = template.getKeyId ().replace ('"', '\0').trim ();
            context.put (key, instance);
        }
        
        return instance;
    }
    
    private AbsEntity defineInstanceFields (AbsEntity entity, DBTemplateRow row, 
            Map <String, AbsEntity> context, boolean full) {
        final List <Field> fields = new ArrayList <> ();
        
        fields.addAll (Arrays.asList  (entity.getClass ().getDeclaredFields ()));
        if (!Object.class.equals (entity.getClass ().getSuperclass ())) {
            Class <?> superType = entity.getClass ().getSuperclass ();
            List <Field> additional = Arrays.asList (superType.getDeclaredFields ());
            fields.addAll (additional);
        }
        
        fields.stream ()
        . filter  (f -> !Modifier.isStatic (f.getModifiers ()))
        . filter  (f -> !Modifier.isFinal (f.getModifiers ()))
        . peek    (f -> f.setAccessible (true))
        . peek    (f -> {
            if (!full) {
                try   { 
                    Object value = f.get (entity);
                    if (value != null) {
                        row.getParams ().put ("@" + f.getName (), value);
                    }
                    f.set (entity, null);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException (e);
                }
            } else if (full && row.getParams ().containsKey ("@" + f.getName ())) {
                try { // restore field value
                    f.set (entity, row.getParams ().get ("@" + f.getName ()));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException (e);
                }
            }
        })
        . filter  (f -> row.getParams ().containsKey (f.getName ()))
        . filter  (f -> full || f.isAnnotationPresent (DBTemplateAnchor.class))
        . forEach (f -> {
            final Class <?> type = f.getType ();
            
            if (!Collection.class.isAssignableFrom (type)) {
                final String value = (String) row.getParams ().get (f.getName ());
                
                try { 
                    if (!f.isAnnotationPresent (DBTemplateConstant.class) || f.get (entity) == null) {
                        f.set (entity, convertParameterValue (type, value, context)); 
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException (e);
                }
            } else {
                final Object tmpValue = row.getParams ().get (f.getName ());
                Collection <String> values = MiscUtils.cast (tmpValue);
                try {
                    @SuppressWarnings ("unchecked")
                    Collection <Object> field = (Collection <Object>) f.get (entity);
                    
                    ParameterizedType generic = MiscUtils.cast (f.getGenericType ());
                    if (generic.getRawType () instanceof Class) {
                        Class <?> gtype = MiscUtils.cast (generic.getRawType ());
                        values.stream ().map     (value -> convertParameterValue (gtype, value, context))
                                        .forEach (field::add);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException (e);
                }
            }
        });
        
        //System.out.println (entity);
        return entity;
    }
    
    private <R> R convertParameterValue (Class <?> required, 
            String value, Map <String, AbsEntity> context) {
        //System.out.println ("Converter of `" + required + "` for '" + value + "'");
        
        if (value == null) { return null; }
        
        if (required.isEnum ()) {
            //System.out.println ("`" + required + "` is enum type");
            try {
                Object result = Enum.valueOf (MiscUtils.cast (required), value);
                return MiscUtils.cast (result);
            } catch (IllegalArgumentException iae) { return null; }
        } else if (LocalDateTime.class.isAssignableFrom (required)) {
            //System.out.println ("`" + required + "` is local date time type");
            if ("{now}".equals (value)) {
                return MiscUtils.cast (LocalDateTime.now (clock));
            } else {
                return MiscUtils.cast (LocalDateTime.parse (value));
            }
        } else if (LocalDate.class.isAssignableFrom (required)) {
            //System.out.println ("`" + required + "` is local date type");
            if ("{now}".equals (value)) {
                return MiscUtils.cast (LocalDate.now (clock));
            } else {
                return MiscUtils.cast (LocalDate.parse (value));
            }
        } else if (Boolean.class.isAssignableFrom (required)) {
            //System.out.println ("`" + required + "` is boolean type");
            value = value.replace ("\"", "");
            return MiscUtils.cast (Boolean.parseBoolean (value));
        } else if (Number.class.isAssignableFrom (required)) {
            //System.out.println ("`" + required + "` is number type");
            String typeName = required.getSimpleName ();
            if ("Integer".equals (typeName)) {
                typeName = "Int";
            }
            
            try {
                Method method = required.getDeclaredMethod ("parse" + typeName, String.class);
                return MiscUtils.cast (method.invoke (null, value.replace ("\"", "")));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException 
                     | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException (e);
            }
        } else if (String.class.isAssignableFrom (required)) {
            //System.out.println ("`" + required + "` is string type");
            final boolean encrypt = value.startsWith ("!!");
            if (encrypt) { value = value.substring (2); }
            
            if (value.length () == 0) {
                return MiscUtils.cast ("");
            }
            
            int left = 0, right = value.length ();
            if (value.startsWith ("\"")) {
                left =  1;
            }
            
            if (value.endsWith ("\"")) {
                right = right - 1;
            }
            
            value = value.substring (left, right);
            return MiscUtils.cast (encrypt ? passwordEncoder.encode (value) : value);
        } else if (value.startsWith ("#")) {
            //System.out.println ("`" + required + "` is reference type");
            value = value.replace ('"', '\0').substring (1).trim ();
            if (!context.containsKey (value)) {
                String message = "Undefined reference `#" + value + "`";
                throw new IllegalStateException (message);
            }
            
            return MiscUtils.cast (context.get (value));
        }
        
        return null;
    }
    
    private static final Pattern DB_TEMPLATE_COLLECTION_PATTERN;
    private static final Pattern DB_TEMPLATE_PARAM_PATTERN;
    private static final Pattern DB_TEMPLATE_KEY_PATTERN;
    
    static {
        final String key = "^([\\w]+)(#\"?([\\w\\s]+)\"?)?:";
        DB_TEMPLATE_KEY_PATTERN = Pattern.compile (key, Pattern.UNICODE_CASE);
        
        final String value = "((#|!!)?\".*?\"|\\$[\\w]+|\\{[\\w]*\\}|(#|!!)?[\\w]+)";
        DB_TEMPLATE_COLLECTION_PATTERN = Pattern.compile (value, Pattern.UNICODE_CASE);
        final String param = "([\\w]+)=(\\[.*?\\]|" + value + "|)";
        DB_TEMPLATE_PARAM_PATTERN = Pattern.compile (param, Pattern.UNICODE_CASE);
    }
    
    private final DBTemplateRow splitInputString (int rowNumber, String input) {
        final String collapsedInput = collapseInputString (input);
        
        Matcher matcher = DB_TEMPLATE_KEY_PATTERN.matcher (collapsedInput);
        if (!matcher.find ()) { return new DBTemplateRow (rowNumber); }
        
        DBTemplateRow row = new DBTemplateRow (rowNumber);
        row.setObjectType (matcher.group (1));
        row.setKeyId (matcher.group (3));
        row.setRow (rowNumber + 1);
        
        matcher = DB_TEMPLATE_PARAM_PATTERN.matcher (collapsedInput);
        while (matcher.find ()) {
            if (!matcher.group (2).startsWith ("[")) { // it's is not collection
                String value = Optional.ofNullable (matcher.group (3)).orElse ("");
                row.getParams ().put (matcher.group (1), value);
            } else {
                final String valuesString = matcher.group (2).substring (1);
                Matcher valuesMatcher = DB_TEMPLATE_COLLECTION_PATTERN
                                      . matcher (valuesString);
                List <String> values = new ArrayList <> ();
                while (valuesMatcher.find ()) {
                    values.add (valuesMatcher.group ());
                }
                
                row.getParams ().put (matcher.group (1), values);
            }
        }
        
        //System.out.println (row);
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
