package ru.shemplo.conduit.appserver.start;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.ServerConstants;
import ru.shemplo.conduit.appserver.entities.AbsEntity;
import ru.shemplo.snowball.utils.MiscUtils;
import ru.shemplo.snowball.utils.StringManip;

@Component
@RequiredArgsConstructor
public class DBValidator {
    
    private final ConfigurableEnvironment configurableEnvironment;
    
    @Transactional public void validate () throws IOException {
        readTemplateFile ();
        /*
        createAdminUserIfNotExists ();
        
        createSystemStudyPeriodIfNotExists ();
        */
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
        catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException (cnfe);
        }
        
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
        
        return instance;
    }
    
    
    private static final Pattern DB_TEMPLATE_PARAM_PATTERN;
    private static final Pattern DB_TEMPLATE_KEY_PATTERN;
    
    static {
        final String key = "^([\\w\\d]+)(#\"?([\\w\\d\\s]+)\"?)?:";
        DB_TEMPLATE_KEY_PATTERN = Pattern.compile (key, Pattern.UNICODE_CASE);
        
        final String param = "([\\w\\d]+)=(\".*?\"|\\$[\\w\\d]+|\\{[\\w\\d]*\\}|[\\w\\d]+|)";
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
    
    /*
    private final PeriodEntityRepository studyPeriodsRepository;
    private final ConfigurableEnvironment configurableEnvironment;
    private final UserEntityRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    
    private void createAdminUserIfNotExists () {
        final String prefix = "server.admin.";
        final Map <String, String> data 
            = Arrays.asList ("login", "phone", "password")
            . stream  ()
            . map     (s -> Pair.mp (s, s))
            . map     (p -> p.applyS (prefix::concat))
            . map     (p -> p.applyS (configurableEnvironment::getProperty))
            . collect (Collectors.toMap (Pair::getF, Pair::getS));
        
        if (data.get ("phone") != null) {
            UserEntity admin = usersRepository.findByPhone (data.get ("phone"));
            if (admin != null) { return; }            
        }
        
        String login    = Optional.ofNullable (data.get ("login")).orElse    ("admin");
        String phone    = Optional.ofNullable (data.get ("phone")).orElse    ("");
        String password = Optional.ofNullable (data.get ("password")).orElse ("admin");
        password = passwordEncoder.encode (password);
        
        UserEntity admin = new UserEntity (login, phone, password, true);
        admin = usersRepository.save (admin);
        UserEntity.setAdmin (admin);
    }
    
    private void createSystemStudyPeriodIfNotExists () {
        final String name = "$system";
        
        PeriodEntity period = studyPeriodsRepository
                                 . findByName (name);
        if (period == null) {
            final UserEntity admin = UserEntity.getAdminEntity ();
            final LocalDateTime from = LocalDateTime.now (clock);
            final PeriodStatus status = PeriodStatus.CREATED;
            
            period = new PeriodEntity (name, "", from, null, status);
            period.setCommitter (admin);
            period.setIssued (from);
            
            period = studyPeriodsRepository.save (period);
        }
        
        PeriodEntity.setSystem (period);
    }
    */
    
}
