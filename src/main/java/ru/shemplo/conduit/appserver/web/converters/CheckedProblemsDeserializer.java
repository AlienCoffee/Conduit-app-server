package ru.shemplo.conduit.appserver.web.converters;

import java.io.IOException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.shemplo.conduit.appserver.web.dto.CheckedOlympiadProblems;
import ru.shemplo.snowball.stuctures.Trio;

@Component
public class CheckedProblemsDeserializer implements Converter <String, CheckedOlympiadProblems> {

    private final ObjectMapper mapper = new ObjectMapper ();

    @Override
    public CheckedOlympiadProblems convert (String value) {
        CheckedOlympiadProblems results = new CheckedOlympiadProblems ();
        
        JsonNode root;
        try   { root = mapper.readTree (value); } 
        catch (IOException e) {
            throw new IllegalStateException (e);
        }
        
        root.forEach (node -> {
            String comment = node.get ("comment").asText ();
            Integer points = node.get ("points").asInt (0);
            Long id = node.get ("id").asLong ();
            
            results.getResults ().add (Trio.mt (id, points, comment));
        });
        
        return results;
    }
    
}
