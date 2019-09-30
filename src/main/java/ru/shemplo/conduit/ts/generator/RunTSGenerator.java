package ru.shemplo.conduit.ts.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import ru.shemplo.snowball.utils.ClasspathManager;

/**
 * Available only from sources (not from JAR)
 * 
 * @author Shemplo
 *
 */
public class RunTSGenerator {
    
    public static void main (String ... args) {
        if (args.length < 1) {
            String message = "Destination path is required as argument";
            throw new IllegalStateException (message);
        }
        
        Path path = Paths.get (args [0]);
        
        if (!Files.exists (path)) {
            try {
                path = Files.createDirectories (path);   
            } catch (Exception e) {
                String message = "Destination path does not exist and cannot be created: " 
                               + e.getMessage ();                
                throw new IllegalStateException (message);
            }
        }
        
        System.out.println ("Destination path: " + args [0]);
        
        if (args.length < 2) {
            String message = "Template HTML elements path is required as argument";
            throw new IllegalStateException (message);
        }
        
        Path pathHTML = Paths.get (args [1]);
        
        if (!Files.exists (pathHTML)) {
            try {
                pathHTML = Files.createDirectories (pathHTML);   
            } catch (Exception e) {
                String message = "Template HTML elements path does not exist and cannot be created: " 
                               + e.getMessage ();                
                throw new IllegalStateException (message);
            }
        }
        
        System.out.println ("Template HTML elements path: " + args [1]);
        
        int modeValue = 0;
        if (args.length > 3) {
            try {
                modeValue = Integer.parseInt (args [1]);
            } catch (NumberFormatException nfe) {
                String message = "Mode value has to be a number (0-2)";
                throw new IllegalStateException (message);
            }
        }
        
        final ClasspathManager cpManager = new ClasspathManager (Arrays.asList ("ru.shemplo"));
        final DTOGenerator dtog = new DTOGenerator (cpManager);
        APIGenerator apig = new APIGenerator (cpManager, dtog);
        HTMLGenerator htmlg = new HTMLGenerator (pathHTML);
        
        switch (modeValue) {
            case 0: {
                System.out.println ("Mode 0 (generate dtos, services and html)");
                printGenerated (dtog,  path, "gen-dtos.ts"); 
                printGenerated (apig,  path, "gen-apis.ts"); 
                printGenerated (htmlg, path, "gen-htmls.ts");
            } break;
            
            case 1: {
                System.out.println ("Mode 1 (generate dtos)");
                printGenerated (dtog, path, "gen-dtos.ts");
            } break;
            
            case 2: {
                System.out.println ("Mode 2 (generate services)");
                printGenerated (apig, path, "gen-apis.ts");
            } break;
        }
        
        System.out.println ("Generation finised");
    }
    
    private static void printGenerated (Generator generator, Path path, String filename) {
        try (
            final PrintWriter dtopw = new PrintWriter (
                new File (path.toFile (), filename)
            );
        ) { generator.print (dtopw); } 
        catch (FileNotFoundException fnfe) {
            String message = "Failed to create generated file";
            throw new IllegalStateException (message);
        }
    }
    
}
