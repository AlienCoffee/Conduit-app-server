package ru.shemplo.conduit.appserver.services;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

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
import ru.shemplo.conduit.appserver.web.dto.CheckedOlympiadProblems;
import ru.shemplo.snowball.stuctures.Pair;
import ru.shemplo.snowball.stuctures.Trio;
import ru.shemplo.snowball.utils.MiscUtils;

//@Slf4j
@Service
@RequiredArgsConstructor
public class OlympaidChecksService extends AbsCachedService <OlympiadCheckEntity> {

    private final OlympiadCheckEntityRepository olympiadChecksRepository;
    private final OlympiadProblemsService olympiadProblemsService;
    private final UsersService usersService;
    private final AccessGuard accessGuard;
    private final Clock clock;
    
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
          . findCheckedProblemsIds (attempt.getId ());
        
        List <OlympiadProblemEntity> problems = olympiadProblemsService
           . getProblemsByOlympiad (attempt.getOlympiad ());
        
        for (OlympiadProblemEntity problem : problems) {
            if (!checkedProblemsId.contains (problem.getId ())) {
                return false; // problem not checked by anybody
            }
        }
        
        return true;
    }
    
    @ProtectedMethod @NotEffectiveMethod
    public Pair <Integer, Integer> getNumberOfCheckedProblemsAndScoreByUser (OlympiadAttemptEntity attempt, WUser user) {
        final PeriodEntity period = attempt.getOlympiad ().getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, user);
        
        Long attemptID = attempt.getId (), userID = user.getId ();
        final Set <Long> problems = olympiadChecksRepository
            . findCheckedProblemsIdsByUser (attemptID, userID);
        Integer score = olympiadChecksRepository.getTotalScoreForAttemptByUser (attemptID, userID);
        return Pair.mp (problems.size (), score);
    }
    
    @Transactional
    @ProtectedMethod @NotEffectiveMethod 
    public void saveAttemptResults (OlympiadAttemptEntity attempt, 
            CheckedOlympiadProblems results, WUser committer) {
        final PeriodEntity period = attempt.getOlympiad ().getGroup ().getPeriod ();
        accessGuard.method (MiscUtils.getMethod (), period, committer);
        
        List <OlympiadCheckEntity> checks = new ArrayList <> ();
        for (Trio <Long, Integer, String> check : results.getResults ()) {
            OlympiadProblemEntity problem = olympiadProblemsService
                                          . getProblem (check.F);
            if (check.S > problem.getCost ()) {
                String message = String.format ("Problem `%s` has cost %d (%d points given)", 
                    problem.getTitle (), problem.getCost (), check.S);
                throw new IllegalArgumentException (message);
            }
            
            OlympiadCheckEntity entity = olympiadChecksRepository
            . findByAttemptAndCommitterAndProblem_Id (attempt, 
                             committer.getEntity (), check.F);
            if (entity == null) {
                entity = new OlympiadCheckEntity ();
                entity.setAttempt (attempt);
                entity.setProblem (problem);
            }
            
            entity.setCommitter (committer.getEntity ());
            entity.setIssued (LocalDateTime.now (clock));
            entity.setComment (check.T);
            entity.setPoints (check.S);
            
            checks.add (entity);
        }
        
        checks.forEach (olympiadChecksRepository::save);
    }
    
}