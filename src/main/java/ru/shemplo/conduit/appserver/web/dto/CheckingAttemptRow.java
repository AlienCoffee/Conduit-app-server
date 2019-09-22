package ru.shemplo.conduit.appserver.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.groups.sheets.SheetAttemptEntity;

@Getter
@ToString
@RequiredArgsConstructor
public class CheckingAttemptRow {
    
    private final SheetAttemptEntity attempt;
    
    private final Integer checkedProblems, checkScore;
    
    private final boolean fullyChecked;
    
}
