package ru.shemplo.conduit.appserver.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.FileEntity;
import ru.shemplo.conduit.appserver.entities.repositories.FileEntityRepository;

//@Slf4j
@Service
@RequiredArgsConstructor
public class FilesService extends AbsCachedService <FileEntity> {
    
    private final FileEntityRepository filesRepository;
    //private final AccessGuard accessGuard;
    //private final Clock clock;
    
    @Override
    protected FileEntity loadEntity (Long id) {
        return filesRepository.findById (id).orElse (null);
    }

    @Override
    protected int getCacheSize () { return 32; }
    
}
