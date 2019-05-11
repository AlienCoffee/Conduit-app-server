package ru.shemplo.conduit.appserver.entities.data;

import static ru.shemplo.conduit.appserver.entities.data.PersonalDataField.*;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PersonalDataTemplate {
    
    STUDENT (Arrays.asList (
                FIRST_NAME, LAST_NAME, GENDER, BIRTHDAY
            ));
    
    @Getter private final List <PersonalDataField> fields;
    
}
