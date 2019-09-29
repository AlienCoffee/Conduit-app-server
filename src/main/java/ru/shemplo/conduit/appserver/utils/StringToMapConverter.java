package ru.shemplo.conduit.appserver.utils;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.shemplo.snowball.utils.MiscUtils;

public class StringToMapConverter implements Converter <String, Map <String, String>> {

    @Override
    public Map <String, String> convert (String source) {
        try {
            final ObjectMapper mapper = new ObjectMapper ();
            return MiscUtils.cast (mapper.readValue (source, Map.class));
        } catch (IOException ioe) {
            throw new IllegalStateException (ioe);
        }
    }
    
}
