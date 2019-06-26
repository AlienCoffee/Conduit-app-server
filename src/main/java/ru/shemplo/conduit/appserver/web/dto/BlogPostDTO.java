package ru.shemplo.conduit.appserver.web.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class BlogPostDTO {
    
    private final String title, content, author;
    
    private final LocalDateTime published;
    
    private List <String> tags = new ArrayList <> ();
    
    private int likes = 0;
    
    private boolean voted = false;
    
}
