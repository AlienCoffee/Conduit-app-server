package ru.shemplo.conduit.appserver.web.form;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class WebFormTitle implements WebFormRow {
    
    private final String title;
    private final String icon;

    @Override
    public String getRowType () { return "title"; }
    
}
