package ru.shemplo.conduit.appserver.entities.data;

import static ru.shemplo.conduit.appserver.entities.data.PersonalDataField.*;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PersonalDataTemplate {
    
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
    
    @Getter private final List <PersonalDataField> fields;
    
}
