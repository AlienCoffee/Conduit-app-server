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
        <title>Conduit</title>
        
        <!--<link rel="stylesheet" href="/resources/css/index.css" />-->
        <!--<link rel="shortcut icon" href="/resources/gfx/jiraf.png" type="image/x-icon">-->
    </head>
    
    <body>
    	<h2>Index</h2>
    	<c:choose>
	    	<c:when test="${user != null}">
	    		<p>Hello: ${user.getName ()}</p>
	    		
	    		<button id="logout">logout</button>,
	    		<a href="/account">account</a>,
	    		<a href="/periods">periods</a>,
	    		<a href="/api/get/personality?userID=2&periodID=0">api personality</a>,
	    		<a href="/api/get/periods">api periods</a>,
	    		<button id="createPeriod">create period</button>,
	    		<a href="/api/get/groups?periodID=0">api groups</a>,
	    		<button id="createGroup">create group</button>
	    	</c:when>
	    	
	    	<c:otherwise>
	    		<a href="/login">login</a>
	    	</c:otherwise>
    	</c:choose>
    	
    	<script src="/resources/js/index.js"></script>
    </body>
</html>