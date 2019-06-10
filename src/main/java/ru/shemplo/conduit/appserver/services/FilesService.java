package ru.shemplo.conduit.appserver.services;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.FileEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadEntity;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.snowball.utils.MiscUtils;

@Service
@RequiredArgsConstructor
public class FilesService {
    
    private final AccessGuard accessGuard;
    
    @ProtectedMethod
    public FileEntity saveOlympiadAttemptArchive (InputStream is, 
            OlympiadEntity olympiad, WUser user) throws IOException {
        accessGuard.method (MiscUtils.getMethod ());
        return null;
    }
    
}
