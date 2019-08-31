package ru.shemplo.conduit.ts.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import ru.shemplo.snowball.utils.ClasspathManager;

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
        
        System.out.println ("Destination path: " + destinationPath);
        
        int modeValue = 0;
        if (args.length > 1) {
            try {
                modeValue = Integer.parseInt (args [1]);
            } catch (NumberFormatException nfe) {
                String message = "Mode value has to be a number (0-3)";
                throw new IllegalStateException (message);
            }
        }
        
        final ClasspathManager cpManager = new ClasspathManager (Arrays.asList ("ru.shemplo"));
        final DTOGenerator dtog = new DTOGenerator (cpManager);
        APIGenerator apig = new APIGenerator (cpManager, dtog);
        
        switch (modeValue) {
            case 0: {
                System.out.println ("Mode 0 (generate dtos and services)");
                printDTOs (dtog, path); printAPIs (apig, path);
            } break;
            
            case 1: {
                //
            } break;
            
            case 2: {
                //
            } break;
        }
    }
    
    private static void printDTOs (DTOGenerator generator, Path path) {
        try (
            final PrintWriter dtopw = new PrintWriter (
                new File (path.toFile (), "gen-dtos.ts")
            );
        ) { generator.print (dtopw); } 
        catch (FileNotFoundException fnfe) {
            String message = "Failed to create DTO file";
            throw new IllegalStateException (message);
        }
    }
    
    private static void printAPIs (APIGenerator generator, Path path) {
        try (
            final PrintWriter dtopw = new PrintWriter (
                new File (path.toFile (), "gen-apis.ts")
            );
        ) { generator.print (dtopw); } 
        catch (FileNotFoundException fnfe) {
            String message = "Failed to create API file";
            throw new IllegalStateException (message);
        }
    }
    
}
