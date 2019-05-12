package ru.shemplo.conduit.appserver.entities.data;

import java.time.LocalDate;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.Named;

@RequiredArgsConstructor
public enum PersonalDataField implements Named {
    
    FIRST_NAME             ("firstName",            true,  Object::toString, __ -> __),
    SECOND_NAME            ("secondName",           true,  Object::toString, __ -> __),
    LAST_NAME              ("lastName",             true,  Object::toString, __ -> __),
    GENDER                 ("gender",               false, Object::toString, __ -> __),
    BIRTHDAY               ("birthday",             false, Object::toString, LocalDate::parse),
    LIVING_REGION          ("livingRegion",         true,  Object::toString, __ -> __),
    LIVING_CITY            ("livingCity",           true,  Object::toString, __ -> __),
    LIVING_ADDRESS         ("livingAddress",        true,  Object::toString, __ -> __),
    STUDY_REGION           ("studyRegion",          true,  Object::toString, __ -> __),
    STUDY_CITY             ("studyCity",            true,  Object::toString, __ -> __),
    STUDY_INSTITUTION      ("studyInstitution",     true,  Object::toString, __ -> __),
    STUDY_INSTITUTION_CODE ("studyInstitutionCode", true,  Object::toString, __ -> __),
    STUDY_FORM             ("studyForm",            false, Object::toString, Integer::parseInt),
    FORM                   ("form",                 false, Object::toString, Integer::parseInt),
    QUALIFICATION          ("qualification",        true,  Object::toString, __ -> __),
    ID_DOC_SERIES          ("idDocSeries",          true,  Object::toString, __ -> __),
    ID_DOC_NUMBER          ("idDocNumber",          true,  Object::toString, __ -> __),
    ID_DOC_SOURCE          ("idDocSource",          true,  Object::toString, __ -> __),
    ID_DOC_SOURCE_CODE     ("idDocSourceCode",      true,  Object::toString, __ -> __),
    ID_DOC_ISSUED          ("idDocIssued",          true,  Object::toString, __ -> __),
    MED_POLICY_NUMBER      ("medPolicyNumber",      true,  Object::toString, __ -> __),
    WORKING_PLACE          ("workingPlace",         true,  Object::toString, __ -> __),
    WORKING_POSITION       ("workingPosition",      true,  Object::toString, __ -> __),
    EXTRA_ACTIVITIES       ("extraActivities",      true,  Object::toString, __ -> __), // Другие математические кружки
    EXTRA_PHONE            ("extraPhone",           true,  Object::toString, __ -> __),
    EXTRA_EMAIL            ("extraEmail",           true,  Object::toString, __ -> __),
    DIETARY_RESTRICTIONS   ("dietaryRestrictions",  true,  Object::toString, __ -> __),
    MEDICAL_RESTRICTIONS   ("medicalRestrictions",  true,  Object::toString, __ -> __),
    CHRONIC_DISEASE        ("chronicDisease",       true,  Object::toString, __ -> __),
    INDIVIDUAL_FEATURES    ("individualFeatures",   true,  Object::toString, __ -> __),
    CHARACTERISTICS        ("characteristics",      true,  Object::toString, __ -> __),
    ACCOMODATION_WISHES    ("accomodationWishes",   true,  Object::toString, __ -> __),
    OTHER_WISHES           ("otherWishes",          true,  Object::toString, __ -> __);
    
    @Getter private final String name;
    @Getter private final boolean empty;
    
    private final Function <Object, String> serializer;
    private final Function <String, Object> deserializer;
    
    public String serialize (Object value) {
        return serializer.apply (value);
    }
    
    public <R> R deserialize (String value) {
        if ((value == null || value.trim ().length () == 0) && !empty) {
            final String message = "Empty string is not allowed";
            throw new IllegalArgumentException (message);
        }
        
        @SuppressWarnings ("unchecked") R result 
            = (R) deserializer.apply (value);
        return result;
    }
    
}
