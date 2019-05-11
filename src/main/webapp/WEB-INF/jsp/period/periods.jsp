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
        <title>Periods</title>
        
        <!--<link rel="stylesheet" href="/resources/css/index.css" />-->
        <!--<link rel="shortcut icon" href="/resources/gfx/jiraf.png" type="image/x-icon">-->
    </head>
    
    <body>
    	<h2>Periods</h2>
    	
    	<a href="/">index</a>
    	
    	<c:forEach var="period" items="${periods}">
    		<div>
    			<p>(${period.getId ()}) <b>${period.getName ()}</b></p>
    			<div><b>Status:</b> ${period.getStatus ()}</div>
    			<div><b>Since:</b> ${period.getSince ()}</div>
   				<div>
   					<b>Links:</b>
   					<a href="/period/${period.getId ()}">info</a>
   					
   					<c:if test="${period.getStatus ().name () eq 'REGISTRATION'}">
   						<a href="/period/${period.getId ()}/registration">register</a>
	    			</c:if>
   				</div>
    		</div>
    	</c:forEach>
    	
    	<script src="/resources/js/periods.js"></script>
    </body>
</html>