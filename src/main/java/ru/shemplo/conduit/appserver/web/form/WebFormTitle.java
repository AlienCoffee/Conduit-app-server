package ru.shemplo.conduit.appserver.web.form;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class WebFormTitle implements WebFormRow {
    
    private final String title;

    @Override
    public String getRowType () { return "title"; }
    
}
