<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML>
<html>
    <head>
    	<meta name="_csrf"        content="${_csrf.token}"/>
		<meta name="_csrf_header" content="${_csrf.headerName}"/>
    
        <meta charset="UTF-8" />
        <title>Olympiad attempts</title>
    </head>
    
    <body>
    	<h2>Check attempt</h2>
    	
    	<a href="/">index</a>
    	<a href="/periods">periods</a>
    	<a href="/period/${period.getId ()}">period</a>
    	<a href="/group/${group.getId ()}">group</a>
    	<a href="/group/${olympiad.getId ()}">olympiad</a>
    	
    	
    	
    	<script src="/resources/js/olympiad.js"></script>
    </body>
</html>