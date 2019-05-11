package ru.shemplo.conduit.appserver;

import java.time.format.DateTimeFormatter;

public class ServerConstants {
    
    public static final DateTimeFormatter RU_DATETIME_FORMAT 
         = DateTimeFormatter.ofPattern ("dd.MM.yyyy HH:mm:ss");
    
    public static final String NO_ENTITY_MESSAGE = "No such entity found";
    
    public static final String $ = "/";
    
    public static final String PAGE_LOGIN = $ + "login";
    public static final String PAGE_PERIODS = $ + "periods";
    public static final String PAGE_PERIOD = $ + "period" + $ + ":id";
    public static final String PAGE_PERIOD_REGISTRATION = PAGE_PERIOD + $ + "registration";
    
    public static final String API = $ + "api";
    public static final String API_ = API + $;
    
    public static final String API_UNCHECKED = API_ + "unchecked";
    public static final String API_UNCHECKED_ = API_UNCHECKED + $;
    
    public static final String API_LOGIN = API_UNCHECKED_ + "login";
    public static final String API_LOGOUT = API_ + "logout";    
    
    public static final String API_GET = API_ + "get";
    public static final String API_GET_ = API_GET + $;
    
    public static final String API_GET_USERS = API_GET_ + "users";
    public static final String API_GET_OPTIONS = API_GET_ + "options";
    public static final String API_GET_ROLES = API_GET_ + "roles";
    public static final String API_GET_PERIODS = API_GET_ + "periods";
    public static final String API_GET_PERSONALITY = API_GET_ + "personality";
    public static final String API_GET_GROUPS = API_GET_ + "groups";
    public static final String API_GET_METHODS = API_GET_ + "methods";
    public static final String API_GET_GUARD_RULES = API_GET_ + "guard-rules";
    
    public static final String API_CREATE = API_ + "create";
    public static final String API_CREATE_ = API_CREATE + $;
    
    public static final String API_CREATE_USER = API_UNCHECKED_ + "create" + $ + "user";
    public static final String API_CREATE_OPTION = API_CREATE_ + "option";
    public static final String API_CREATE_ROLE = API_CREATE_ + "role";
    public static final String API_CREATE_PERIOD = API_CREATE_ + "period";
    public static final String API_CREATE_GROUP = API_CREATE_ + "group";
    
    public static final String API_UPDATE = API_ + "update";
    public static final String API_UPDATE_ = API_UPDATE + $;
    
    public static final String API_UPDATE_ADD = API_UPDATE_ + "add";
    public static final String API_UPDATE_ADD_ = API_UPDATE_ADD + $;
    public static final String API_UPDATE_REMOVE = API_UPDATE_ + "remove";
    public static final String API_UPDATE_REMOVE_ = API_UPDATE_REMOVE + $;
    
    public static final String API_UPDATE_ADD_METHOD_RULE = API_UPDATE_ADD_ + "method-rule";
    public static final String API_UPDATE_REMOVE_METHOD_RULE = API_UPDATE_REMOVE_ + "method-rule";
    public static final String API_UPDATE_ADD_ROLE_OPTION = API_UPDATE_ADD_ + "role-option";
    public static final String API_UPDATE_REMOVE_ROLE_OPTION = API_UPDATE_REMOVE_ + "role-option";
    public static final String API_UPDATE_ADD_ROLE_TO_USER = API_UPDATE_ADD_ + "role-to-user";
    public static final String API_UPDATE_REMOVE_ROLE_FROM_USER = API_UPDATE_REMOVE_ + "role-from-user";
    
}
