package ru.shemplo.conduit.ts.generator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RunTSGenerator {
    
    public static void main (String ... args) {
        if (args.length < 1) {
            String message = "Destination path is required as argument";
            throw new IllegalStateException (message);
        }
        
        final String destinationPath = args [0];
        Path path = Paths.get (destinationPath);
        
        if (!Files.exists (path)) {
            try {
                path = Files.createDirectories (path);   
            } catch (Exception e) {
                String message = "Destination path does not exist and cannot be created: " 
                               + e.getMessage ();                
                throw new IllegalStateException (message);
            }
            
        }
    }
    
}
