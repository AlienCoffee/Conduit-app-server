package ru.shemplo.conduit.appserver.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class AttachmentFileRow {
    
    private final String name, path, size;
    
    // created, last modified, ...
    
}
