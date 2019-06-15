package ru.shemplo.conduit.appserver.services;

import static ru.shemplo.conduit.appserver.ServerConstants.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shemplo.conduit.appserver.entities.FileEntity;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.repositories.FileEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilesService extends AbsCachedService <FileEntity> {
    
    private final FileEntityRepository filesRepository;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
    @Override
    protected FileEntity loadEntity (Long id) {
        return filesRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    @ProtectedMethod
    public FileEntity saveOlympiadAttemptArchive (MultipartFile file,
            OlympiadEntity olympiad, WUser user) throws IOException {
        accessGuard.method (MiscUtils.getMethod (), user);
        if (!ATTEMPTS_DIR.isDirectory ()) {
            ATTEMPTS_DIR.mkdir ();
        }
        
        String filename = String.format ("attempt_%s_%s.zip", user.getUsername (), 
                                         clock.millis ());
        final File desc = new File (ATTEMPTS_DIR, filename);
        Files.write (desc.toPath (), file.getBytes ());
        boolean valid = true;
        int errorNo = -1;
        
        try (                
            final ZipFile zdesc = new ZipFile (desc);
        ) {
            Enumeration <? extends ZipEntry> entries = zdesc.entries ();
            int amoutn = 0;
            
            while (entries.hasMoreElements ()) {
                ZipEntry entry = entries.nextElement ();
                amoutn += 1;
                
                if (entry.getSize () >= 5_000_000) { // ~ 5Mb
                    valid = false; errorNo = 0;
                    break;
                }
                
                // TODO: check of file type (at least by extension)
            }
            
            if (amoutn == 0) {
                String message = "Attached archive is empty";                
                throw new IllegalStateException (message);
            }
        } catch (IOException ioe) {
            final String reason = ioe.getMessage () != null ? ioe.getMessage () : "unknown";
            String message = String.format ("Failed to read archive (reason: %s)", reason);
            throw new IOException (message);
        }
        
        if (!valid) {
            desc.delete ();
            
            if (errorNo == 0) {
                String message = "Size of separate file can't be more 5Mb";                
                throw new IllegalStateException (message);
            }
        }
        
        FileEntity entity = new FileEntity (filename, desc.getPath ());
        entity.setIssued (LocalDateTime.now (clock));
        entity.setCommitter (user.getEntity ());
        
        log.info (entity.toTemplateString ());
        return filesRepository.save (entity);
    }
    
    @ProtectedMethod
    public List <ZipEntry> getEntriesInAttemptArchive (OlympiadAttemptEntity attempt, WUser user) {
        final PeriodEntity period = attempt.getOlympiad ().getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        FileEntity file = attempt.getAttachments ().get (0);
        List <ZipEntry> names = new ArrayList <> ();
        
        try (                
            final ZipFile zdesc = new ZipFile (file.toFile ());
        ) {
            ///////////////////////////////////////
            zdesc.stream ().forEach (names::add);//
            ///////////////////////////////////////
        } catch (IOException ioe) {
            final String reason = ioe.getMessage () != null ? ioe.getMessage () : "unknown";
            String message = String.format ("Failed to read archive (reason: %s)", reason);
            throw new IllegalStateException (message);
        }
        
        return names;
    }
    
}
