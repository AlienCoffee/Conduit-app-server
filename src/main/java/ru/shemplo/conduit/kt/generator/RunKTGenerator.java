package ru.shemplo.conduit.kt.generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import ru.shemplo.conduit.ts.generator.Generator;
import ru.shemplo.snowball.utils.ClasspathManager;

public class RunKTGenerator {
    
    public static void main (String ... args) {
        if (args.length < 1) {
            String message = "Destination file is required as argument";
            throw new IllegalStateException (message);
        }
        
        Path path = Paths.get (args [0]);
        
        if (!Files.exists (path)) {
            try {
                path = Files.createFile (path);   
            } catch (Exception e) {
                String message = "Destination file does not exist and cannot be created: " 
                               + e.getMessage ();                
                throw new IllegalStateException (message);
            }
        }
        
        System.out.println ("Destination file: " + args [0]);
        
        final ClasspathManager cpManager = new ClasspathManager (Arrays.asList ("ru.shemplo"));
        printGenerated (new KTExtensionsGenerator (cpManager), path);
        System.out.println ("Generation finised");
    }
    
    private static void printGenerated (Generator generator, Path path) {
        try (
            final PrintWriter dtopw = new PrintWriter (path.toFile ());
        ) { generator.print (dtopw); } 
        catch (FileNotFoundException fnfe) {
            String message = "Failed to create generated file";
            throw new IllegalStateException (message);
        }
    }
    
}
