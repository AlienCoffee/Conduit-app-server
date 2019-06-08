package ru.shemplo.conduit.appserver;

import java.io.IOException;
import java.time.Clock;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import ru.shemplo.conduit.appserver.entities.repositories.GroupEntityRepository;
import ru.shemplo.conduit.appserver.start.DBValidator;
import ru.shemplo.conduit.appserver.start.MethodsScanner;

@SpringBootApplication (exclude = {SecurityAutoConfiguration.class})
public class RunAppServer {
    
    /*
     * При регистрации создаётся какая-то Personality (по выбору юзера)
     * 
     * Далее, модератор может пользователю назначить роль для того периода,
     * в котором пользователь создал персональ. У роли есть ссылка на тип
     * необходиомй персонали.
     * 
     * После назначения роли пользователь становится полонценным участником
     * определённого типа (ученик, преподаватель, оргкомитет) с заполненной
     * уже персональю.
     * 
     * У пользователя в одном периоде может быть несколько ролей (и персоналей),
     * например "преподаватель" и "оргкомитет". При просмотре персональных
     * данных поля будут просто объединяться.
     * 
     */
    
    public static void main (String ... args) throws BeansException, IOException {
        final Class <?> MAIN_CLASS = RunAppServer.class;
        final ConfigurableApplicationContext context 
            = SpringApplication.run (MAIN_CLASS, args);
        
        context.getBean (MethodsScanner.class).scanMethods (context);
        context.getBean (DBValidator.class).validate ();
        
        System.out.println (context.getBean (GroupEntityRepository.class));
    }
    
    @Bean public static Clock getSystemClock () {
        return Clock.systemDefaultZone ();
    }
    
}
