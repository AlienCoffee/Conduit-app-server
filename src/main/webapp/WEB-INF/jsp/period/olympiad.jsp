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
        <title>Olympiad</title>
        
        <!--<link rel="stylesheet" href="/resources/css/index.css" />-->
        <!--<link rel="shortcut icon" href="/resources/gfx/jiraf.png" type="image/x-icon">-->
    </head>
    
    <body>
    	<h2>Olympiad : ${olympiad.getName ()}</h2>
    	
    	<a href="/">index</a>
    	<a href="/periods">periods</a>
    	<a href="/period/${period.getId ()}">period</a>
    	<a href="/group/${group.getId ()}">group</a>
    	
    	<pre>${olympiad.getDescription ()}</pre>
    	<p><b>Send attempts</b>   : ${olympiad.getAttemptsLimit ()}</p>
    	<p><b>Olympiad author</b> : ${olympiad.getCommitter ().getLogin ()}</p>
    	
    	<h3>Problems</h3>
    	
    	<c:choose>
    		<c:when test="${problems != null && not empty problems}">
    			<c:forEach var="problem" items="${problems}">
    				<div>
		    			<p>(${problem.getId ()}) <b>${problem.getTitle ()}</b></p>
		    			<pre>${problem.getContent ()}</pre>
		    			<div><b>Author:</b> ${problem.getCommitter ().getLogin ()}</div>
		    			<div><b>Attachments:</b> ${problem.getAttachments ().size ()}</div>
		    			<div><b>Difficulty:</b> ${problem.getDifficulty ()}</div>
		    			<div><b>Cost:</b> ${problem.getCost ()}</div>
		    		</div>
    			</c:forEach>
    		</c:when>
    		
    		<c:otherwise>
    			<p>No problems in olympiad</p>
    		</c:otherwise>
    	</c:choose>
    	
    	<script src="/resources/js/period.js"></script>
    </body>
</html>