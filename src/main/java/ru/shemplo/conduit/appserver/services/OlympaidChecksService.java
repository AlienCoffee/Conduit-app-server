package ru.shemplo.conduit.appserver.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.PeriodEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadCheckEntity;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadProblemEntity;
import ru.shemplo.conduit.appserver.entities.repositories.OlympiadCheckEntityRepository;
import ru.shemplo.conduit.appserver.entities.wrappers.WUser;
import ru.shemplo.conduit.appserver.security.AccessGuard;
import ru.shemplo.conduit.appserver.security.ProtectedMethod;
import ru.shemplo.conduit.appserver.utils.NotEffectiveMethod;
import ru.shemplo.snowball.utils.MiscUtils;

//@Slf4j
@Service
@RequiredArgsConstructor
public class OlympaidChecksService extends AbsCachedService <OlympiadCheckEntity> {

    private final OlympiadCheckEntityRepository olympiadChecksRepository;
    private final OlympiadProblemsService olympiadProblemsService;
    private final UsersService usersService;
    private final AccessGuard accessGuard;
    
    @Override
    protected OlympiadCheckEntity loadEntity (Long id) {
        return null;
    }

    @Override
    protected int getCacheSize () { return 32; }
    
    @ProtectedMethod @NotEffectiveMethod
    public boolean isAttemptChecked (OlympiadAttemptEntity attempt) {
        final PeriodEntity period = attempt.getOlympiad ().getGroup ().getPeriod ();
        final WUser user = usersService.getUser (attempt.getUser ().getId ());
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        Set <Long> checkedProblemsId = olympiadChecksRepository
          . findCheckedProblemsIds (attempt);
        
        List <OlympiadProblemEntity> problems = olympiadProblemsService
           . getProblemsByOlympiad (attempt.getOlympiad ());
        
        for (OlympiadProblemEntity problem : problems) {
            if (!checkedProblemsId.contains (problem.getId ())) {
                return false; // problem not checked by anybody
            }
        }
        
        return true;
    }
    
}
