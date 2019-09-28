package ru.shemplo.conduit.appserver.entities.data;

import static ru.shemplo.conduit.appserver.entities.data.PersonalDataField.*;
import static ru.shemplo.conduit.appserver.web.form.WebFormField.WebFormFieldType.*;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.web.form.WebFormField;
import ru.shemplo.conduit.appserver.web.form.WebFormRow;
import ru.shemplo.conduit.appserver.web.form.WebFormSelect;
import ru.shemplo.conduit.appserver.web.form.WebFormTitle;

@RequiredArgsConstructor
public enum PersonalDataTemplate {
    
    STUDENT ("Student", true, Arrays.asList (
            new WebFormTitle ("Personal data", "fas fa-address-card"),
            new WebFormField <> (LAST_NAME, TEXT, "Фамилия", null, true),
            new WebFormField <> (FIRST_NAME, TEXT, "Имя", null, true),
            new WebFormField <> (SECOND_NAME, TEXT, "Отчество", null, false),
            new WebFormField <> (BIRTHDAY, DATE, "Дата рождения", null, true),
            new WebFormField <> (GENDER, TEXT, "Пол", "Одной буквой: М - мужской, Ж - женский", true),
            new WebFormField <> (STUDY_FORM, NUMBER, "Класс обучения", "Класс, который школьник закончил", true),
            new WebFormTitle ("ID document", "fas fa-id-card"),
            new WebFormSelect ("Document type", "documentType", "Pasport", "Sertificate of Birth"),
            new WebFormField <> (SECOND_NAME, TEXT, "Series", null, false),
            new WebFormField <> (SECOND_NAME, TEXT, "Number", null, false),
            new WebFormTitle ("Living place", "fas fa-home"),
            new WebFormField <> (SECOND_NAME, TEXT, "Region", null, false),
            new WebFormField <> (SECOND_NAME, TEXT, "City / Town", null, false)
        )),
    
    TEACHER ("Teacher", true, Arrays.asList (
            new WebFormTitle ("Персональные данные", null),
            new WebFormField <> (LAST_NAME, TEXT, "Фамилия", null, true),
            new WebFormField <> (FIRST_NAME, TEXT, "Имя", null, true),
            new WebFormField <> (SECOND_NAME, TEXT, "Отчество", null, false),
            new WebFormField <> (GENDER, TEXT, "Пол", "Одной буквой: М - мужской, Ж - женский", true),
            new WebFormField <> (BIRTHDAY, DATE, "Дата рождения", null, true)
        )),
    
    PARENT_MOTHER ("Parent (mother)", true, Arrays.asList (
            new WebFormTitle ("Персональные данные", null),
            new WebFormField <> (LAST_NAME, TEXT, "Фамилия", null, true),
            new WebFormField <> (FIRST_NAME, TEXT, "Имя", null, true),
            new WebFormField <> (SECOND_NAME, TEXT, "Отчество", null, false),
            new WebFormField <> (GENDER, TEXT, "Пол", "Одной буквой: М - мужской, Ж - женский", true),
            new WebFormField <> (BIRTHDAY, DATE, "Дата рождения", null, true)
        )),
    
    PARENT_FATHER ("Parent (father)", true, Arrays.asList (
            new WebFormTitle ("Персональные данные", null),
            new WebFormField <> (LAST_NAME, TEXT, "Фамилия", null, true),
            new WebFormField <> (FIRST_NAME, TEXT, "Имя", null, true),
            new WebFormField <> (SECOND_NAME, TEXT, "Отчество", null, false),
            new WebFormField <> (GENDER, TEXT, "Пол", "Одной буквой: М - мужской, Ж - женский", true),
            new WebFormField <> (BIRTHDAY, DATE, "Дата рождения", null, true)
        ));
    
    @Getter private final String name;
    @Getter private final boolean vivsble;
    @Getter private final List <WebFormRow> rows;
    
    public int getNumberOfRequired () {
        return (int) rows.stream ()
             . filter (row -> (row instanceof WebFormField) 
                           && ((WebFormField <?>) row).isRequired ())
             . count  ();
    }
    
    public static PersonalDataTemplate forName (String name) {
        return Arrays.asList (values ()).stream ()
             . filter      (temp -> temp.name.equals (name)).findFirst   ()
             . orElseThrow (() -> new EntityNotFoundException ("Bad template name"));
    }
    
}
