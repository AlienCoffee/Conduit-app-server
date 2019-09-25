package ru.shemplo.conduit.ts.generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.utils.fp.StreamUtils;

public class HTMLGenerator implements Generator {
    
    private final AtomicInteger counter = new AtomicInteger ();
    private List <Pair <String, Document>> docs;
    private final Path source;
    
    public HTMLGenerator (Path source) {
        this.source = source;
        initialize ();
    }
    
    private void initialize () {
        try {
            docs = Files.walk (source).map (path -> {
                try {
                    if (Files.isDirectory (path)) {
                        return null;
                    }
                    
                    final String fileName = path.toFile ().getName ();
                    final byte [] bytes = Files.readAllBytes (path);
                    return Pair.mp (fileName, bytes);
                } catch (IOException ioe) {
                    throw new RuntimeException (ioe);
                }
            }).filter (Objects::nonNull)
            . map (pair -> pair.applyS (bytes -> new String (bytes, StandardCharsets.UTF_8)))
            . map (pair -> pair.applyS (Jsoup::parse)).collect (Collectors.toList ());
        } catch (IOException | RuntimeException ioe) {
            throw new RuntimeException (ioe);
        }
    }
    
    @Override
    public void print (PrintWriter pw) {
        docs.sort (Comparator.comparing (Pair::getF));
        docs.forEach (pair -> {
            String elementName = Arrays.asList (pair.getF ().replace (".html", "").split ("-")).stream ()
                               . map (str -> Character.toUpperCase (str.charAt (0)) + str.substring (1))
                               . collect (Collectors.joining ());
            final Document doc = pair.getS ();
            
            String parameters = getParameters (doc).stream ().map (p -> p + " : string")
                              . collect (Collectors.joining (", "));
            pw.println (String.format ("export function make%s (%s) : HTMLElement {", 
                    elementName, parameters));
            String root = processElement (doc.body (), null, pw);
            pw.println (String.format ("    return %s;", root));
            pw.println ("}");
            pw.println ();
        });
    }
    
    private List <String> getParameters (Document doc) {
        Pattern pattern = Pattern.compile ("%(\\w+)%", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher (doc.html ());
        List <String> parameters = StreamUtils.whilst (m -> m.find (), m -> m.group (1), matcher)
                                 . collect (Collectors.toList ());
        return parameters;
    }
    
    private String processElement (Element element, String parent, PrintWriter pw) {
        if (element.tagName ().equals ("body")) {
            if (element.childNodeSize () > 0) {
                final Element child = element.child   (0);
                return processElement (child, parent, pw);
            }
        }
        
        String tagName = element.tagName ();
        String objName = tagName + counter.getAndIncrement ();
        pw.println (String.format ("    let %s = document.createElement (\"%s\");", objName, tagName));
        if (parent != null && parent.length () > 0) {
            pw.println (String.format ("    %s.appendChild (%s)", parent, objName));
        }
        
        for (Attribute attr : element.attributes ()) {
            final String attrKey = attr.getKey (), attrValue = attr.getValue ();
            if (attrValue.startsWith ("%") && attrValue.endsWith ("%")) {
                String value = attrValue.substring (1, attrValue.length () - 1);
                pw.println (String.format ("    %s.setAttribute (\"%s\", %s);", 
                    objName, attrKey, value));
            } else {                
                pw.println (String.format ("    %s.setAttribute (\"%s\", \"%s\");", 
                    objName, attrKey, attrValue));
            }
        }
        if (element.children ().size () == 0) {
            String value = element.text ();
            if (value.startsWith ("%") && value.endsWith ("%")) {
                value = value.substring (1, value.length () - 1);
                pw.println (String.format ("    %s.innerHTML = %s;", objName, value));
            } else if (value.trim ().length () > 0) {
                pw.println (String.format ("    %s.innerHTML = \"%s\";", 
                    objName, value));
            }
        }
        pw.println ();
        
        for (Element child : element.children ()) {
            processElement (child, objName, pw);
        }
        
        
        return objName;
    }
    
}
