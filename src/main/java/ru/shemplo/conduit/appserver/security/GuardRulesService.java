package ru.shemplo.conduit.appserver.security;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.GuardRuleEntity;
import ru.shemplo.conduit.appserver.entities.repositories.GuardRuleEntityRepository;
import ru.shemplo.conduit.appserver.utils.ExtendedLRUCache;

@Service
@RequiredArgsConstructor
public class GuardRulesService {
    
    private final GuardRuleEntityRepository rulesRepository;
    
    private final ExtendedLRUCache <String, GuardRuleEntity> CACHE
        = new ExtendedLRUCache<> (128, GuardRuleEntity::getObject);
    
    protected Optional <GuardRuleEntity> getRule (String object) {
        return Optional.ofNullable (CACHE.getOrPut (object, () -> {
            return rulesRepository.findByObject (object);
        }));
    }
    
    protected void invalidate () { CACHE.invalidate (); }
    
}
