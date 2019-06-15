package ru.shemplo.conduit.appserver.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.groups.olympiads.OlympiadAttemptEntity;

@Getter
@ToString
@RequiredArgsConstructor
public class CheckingAttemptRow {
    
    private final OlympiadAttemptEntity attempt;
    
    private final Integer checkedProblems, checkScore;
    
}
