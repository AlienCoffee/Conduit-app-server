package ru.shemplo.conduit.appserver.start;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@RequiredArgsConstructor
public class DBTemplateRow {
    
    private String objectType, keyId;
    private final int numberId;
    private int row;
    
    private final Map <String, Object> params = new HashMap <> ();
    
}
