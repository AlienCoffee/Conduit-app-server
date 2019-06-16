package ru.shemplo.conduit.appserver.web.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import ru.shemplo.snowball.stuctures.Trio;

@ToString
@Getter @Setter
@NoArgsConstructor
public class CheckedOlympiadProblems {
    
    private final List <Trio <Long, Integer, String>> results = new ArrayList <> ();
    
}
