package ru.shemplo.conduit.appserver.entities.data;

import java.time.LocalDate;
import java.util.function.BiConsumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PersonalDataField {
    
    FIRST_NAME             ("firstName",            true,  (data, v) -> data.setFirstName (v)),
    SECOND_NAME            ("secondName",           true,  (data, v) -> data.setSecondName (v)),
    LAST_NAME              ("lastName",             true,  (data, v) -> data.setLastName (v)),
    GENDER                 ("gender",               false, (data, v) -> data.setGender (v.charAt (0))),
    BIRTHDAY               ("birthday",             false, (data, v) -> data.setBirthday (LocalDate.parse (v))),
    LIVING_REGION          ("livingRegion",         true,  (data, v) -> data.setLivingRegion (v)),
    LIVING_CITY            ("livingCity",           true,  (data, v) -> data.setLivingCity (v)),
    LIVING_ADDRESS         ("livingAddress",        true,  (data, v) -> data.setLivingAddress (v)),
    STUDY_REGION           ("studyRegion",          true,  (data, v) -> data.setStudyRegion (v)),
    STUDY_CITY             ("studyCity",            true,  (data, v) -> data.setStudyCity (v)),
    STUDY_INSTITUTION      ("studyInstitution",     true,  (data, v) -> data.setStudyInstitution (v)),
    STUDY_INSTITUTION_CODE ("studyInstitutionCode", true,  (data, v) -> data.setStudyInstitutionCode (v)),
    FORM                   ("form",                 false, (data, v) -> data.setForm (Integer.parseInt (v))),
    QUALIFICATION          ("qualification",        true,  (data, v) -> data.setQualification (v)),
    ID_DOC_SERIES          ("idDocSeries",          true,  (data, v) -> data.setIdDocSeries (v)),
    ID_DOC_NUMBER          ("idDocNumber",          true,  (data, v) -> data.setIdDocNumber (v)),
    ID_DOC_SOURCE          ("idDocSource",          true,  (data, v) -> data.setIdDocSource (v)),
    ID_DOC_SOURCE_CODE     ("idDocSourceCode",      true,  (data, v) -> data.setIdDocSourceCode (v)),
    ID_DOC_ISSUED          ("idDocIssued",          true,  (data, v) -> data.setIdDocIssued (LocalDate.parse (v))),
    MED_POLICY_NUMBER      ("medPolicyNumber",      true,  (data, v) -> data.setMedPolicyNumber (v)),
    WORKING_PLACE          ("workingPlace",         true,  (data, v) -> data.setWorkingPlace (v)),
    WORKING_POSITION       ("workingPosition",      true,  (data, v) -> data.setWorkingPosition (v)),
    EXTRA_PHONE            ("extraPhone",           true,  (data, v) -> data.setExtraPhone (v)),
    EXTRA_EMAIL            ("extraEmail",           true,  (data, v) -> data.setExtraEmail (v))
    ;
    
    @Getter private final String key;
    @Getter private final boolean empty;
    @Getter private final BiConsumer <PersonalDataEntity, String> setter;
    
}
