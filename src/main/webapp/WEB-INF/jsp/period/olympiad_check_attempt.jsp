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
    	<a href="/olympiad/${olympiad.getId ()}">olympiad</a>
    	
    	<h3>Attachment files</h3>
    	
    	<c:choose>
    		<c:when test="${not empty files}">
    			<ul>
	    			<c:forEach var="file" items="${files}">
	    				<li>
	    					<a href="file/${file.getName ()}" target="_blank">
	    						${file.getName ()}
    						</a>
	    				</li>
	    			</c:forEach>
    			</ul>
    		</c:when>
    		
    		<c:otherwise>
    			<p>Failed to unzip archive</p>
    		</c:otherwise>
    	</c:choose>
    	
    	<h3>Results form</h3>
    	
    	<c:forEach var="problem" items="${problems}">
    		<p>
    			(${problem.getId ()})
    			<input class="attempt-problem-id" type="hidden" 
    				   value="${problem.getId ()}" />
    			<input class="attempt-problem-points" type="number" 
    				   min="0" max="${problem.getCost ()}" />
    			<sub>(max. ${problem.getCost ()})</sub> 
    			<input class="attempt-problem-comment" type="text" 
    				   placeholder="comment"/> - 
    			${problem.getTitle ()}
   			</p>
    	</c:forEach>
    	
    	<p>
    		<button onclick="saveResults ()">save</button>
    	</p>
    	
    	<input id="attempt-id" type="hidden" value="${attempt.getId ()}">
    	<script src="/resources/js/olympiad.js"></script>
    </body>
</html>