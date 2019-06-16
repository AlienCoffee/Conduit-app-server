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
    	<h2>Attempts of olympiad "${olympiad.getName ()}"</h2>
    	
    	<a href="/">index</a>
    	<a href="/periods">periods</a>
    	<a href="/period/${period.getId ()}">period</a>
    	<a href="/group/${group.getId ()}">group</a>
    	<a href="/olympiad/${olympiad.getId ()}">olympiad</a>
    	
    	<c:choose>
    		<c:when test="${attempts != null && not empty attempts}">
    			<c:forEach var="row" items="${attempts}">
    				<p>
    					(${row.getAttempt ().getId ()}) 
    					Attempt from <u>${row.getAttempt ().getUser ().getLogin ()}</u>
    					at <u>${row.getAttempt ().getIssued ()}</u>
    					
    					<c:choose>
    						<c:when test="${row.getCheckedProblems () > 0}">
    							(problems: ${row.getCheckedProblems ()},
    							points: ${row.getCheckScore ()}, 
    						</c:when>
    					
    						<c:otherwise>
    							(not checked, 
    						</c:otherwise>
    					</c:choose>
    					
    					<c:choose>
    						<c:when test="${row.isFullyChecked ()}">
    							fully checked)
    						</c:when>
    					
    						<c:otherwise>
    							need check) 
    						</c:otherwise>
    					</c:choose>
    					
    					<a href="/attempt/${row.getAttempt ().getId ()}/check">check</a>
   					</p>
    			</c:forEach>
    		</c:when>
    		
    		<c:otherwise>
    			<p>No attempts available for check now</p>
    		</c:otherwise>
    	</c:choose>
    	
    	<script src="/resources/js/olympiad.js"></script>
    </body>
</html>