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
import ru.shemplo.conduit.appserver.web.form.WebFormTitle;

@RequiredArgsConstructor
public enum PersonalDataTemplate {
    
    /*
    STUDENT (Arrays.asList (
                FIRST_NAME, LAST_NAME, GENDER, BIRTHDAY, STUDY_FORM,
                LIVING_REGION, LIVING_CITY, LIVING_ADDRESS,
                MED_POLICY_NUMBER, ID_DOC_SERIES, ID_DOC_NUMBER,
                FORM, STUDY_INSTITUTION, STUDY_INSTITUTION_CODE, STUDY_REGION, STUDY_CITY,
                DIETARY_RESTRICTIONS, MEDICAL_RESTRICTIONS, CHRONIC_DISEASE,
                INDIVIDUAL_FEATURES, CHARACTERISTICS, 
                ACCOMODATION_WISHES, OTHER_WISHES
            )),
    
    TEACHER (Arrays.asList (
                FIRST_NAME, LAST_NAME, GENDER, BIRTHDAY,
                LIVING_REGION, LIVING_CITY,
                MED_POLICY_NUMBER, ID_DOC_SERIES, ID_DOC_NUMBER,
                QUALIFICATION,
                ACCOMODATION_WISHES, OTHER_WISHES
            ));
            */
    
    STUDENT ("Школьник", true, Arrays.asList (
            new WebFormTitle ("Данные об школьнике"),
            new WebFormField <> (LAST_NAME, TEXT, "Фамилия школьника", null, true),
            new WebFormField <> (FIRST_NAME, TEXT, "Имя школьника", null, true),
            new WebFormField <> (SECOND_NAME, TEXT, "Отчество школьника", null, false),
            new WebFormField <> (GENDER, TEXT, "Пол", "Одной буквой: М - мужской, Ж - женский", true),
            new WebFormField <> (BIRTHDAY, DATE, "Дата рождения", null, true),
            new WebFormField <> (STUDY_FORM, NUMBER, "Класс обучения", "Класс, который школьник закончил", true)
        )),
    
    TEACHER ("Преподаватель", true, Arrays.asList (
            new WebFormTitle ("Персональные данные"),
            new WebFormField <> (LAST_NAME, TEXT, "Фамилия", null, true),
            new WebFormField <> (FIRST_NAME, TEXT, "Имя", null, true),
            new WebFormField <> (SECOND_NAME, TEXT, "Отчество", null, false),
            new WebFormField <> (GENDER, TEXT, "Пол", "Одной буквой: М - мужской, Ж - женский", true),
            new WebFormField <> (BIRTHDAY, DATE, "Дата рождения", null, true)
        )),
    
    PARENT_MOTHER ("Родитель (мать)", true, Arrays.asList (
            new WebFormTitle ("Персональные данные"),
            new WebFormField <> (LAST_NAME, TEXT, "Фамилия", null, true),
            new WebFormField <> (FIRST_NAME, TEXT, "Имя", null, true),
            new WebFormField <> (SECOND_NAME, TEXT, "Отчество", null, false),
            new WebFormField <> (GENDER, TEXT, "Пол", "Одной буквой: М - мужской, Ж - женский", true),
            new WebFormField <> (BIRTHDAY, DATE, "Дата рождения", null, true)
        )),
    
    PARENT_FATHER ("Родитель (отец)", true, Arrays.asList (
            new WebFormTitle ("Персональные данные"),
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
