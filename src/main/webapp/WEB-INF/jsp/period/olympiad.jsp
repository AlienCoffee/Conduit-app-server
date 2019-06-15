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
    	<c:if test="${user.getEntity ().getIsAdmin ()}">
    		<a href="/olympiad/${olympiad.getId ()}/attempts">attempts for check</a>
    		<c:choose>
    			<c:when test="${!olympiad.isResultsFinalized ()}">    			
		    		<button onclick="toggleResults(true)">finalize results</button>
    			</c:when>
    			
    			<c:otherwise>
    				<button onclick="toggleResults(false)">invalidate results</button>
    			</c:otherwise>
    		</c:choose>
    	</c:if>
    	<c:if test="${olympiad.isResultsFinalized () && olympiad.isResultsVisible ()}">
    		<a href="/olympiad/${olympiad.getId ()}/results">results</a>
    	</c:if>
    	
    	<pre>${olympiad.getDescription ()}</pre>
    	<p><b>Attempts number limit</b>: ${olympiad.getAttemptsLimit ()}</p>
    	<p><b>Olympiad author</b>: ${olympiad.getCommitter ().getLogin ()}</p>
    	<p><b>Till</b>: ${olympiad.getFinished ()}</p>
    	
    	<h3>Make attempt</h3>
    	
    	<c:choose>
    		<c:when test="${remaining_attempts > 0 && !is_olympiad_finished}">    		
		    	<p><b>Remaining attempts</b>: ${remaining_attempts}</p>
		    	
		    	<p>
		    		Only <b>.zip</b> archives with images (<b>.png</b>, <b>.jpg</b>) 
		    		and text files (<b>.pdf</b>) is allowed
	    		</p>
		    	
		    	<p>
		    		<input id="attempt-comment-input" type="text" />
		    		<input id="attempt-file-input" type="file" accept="application/zip" />
		    		<button id="send-attempt">send</button>
		    	</p>
    		</c:when>
    		
    		<c:when test="${is_olympiad_finished}">    		
		    	<p>Olympiad time is over</p>
    		</c:when>
    		
    		<c:when test="${remaining_attempts == 0}">    		
    			<p>You have reached attempts limit</p>
    		</c:when>
    	</c:choose>
    	
    	<h3>Follow attempts</h3>
		    	
    	<table border="1">
    		<caption>Your attempts</caption>
    		
    		<tr>
    			<td>#</td>
    			<td>Issued</td>
    			<td>Status</td>
    			<td>Comment</td>
    		</tr>
    		
    		<c:choose>
    			<c:when test="${not empty attempts}">
    				<c:forEach var="attempt" items="${attempts}">
    					<tr>
    						<td>${attempt.getId ()}</td>
    						<td>${attempt.getIssued ()}</td>
    						<td>${attempt.getStatus ()}</td>
    						<td>${attempt.getReason ()}</td>
    					</tr>
    				</c:forEach>
    			</c:when>
    			
    			<c:otherwise>
    				<tr>
    					<td colspan="4">No attempts now</td>
    				</tr>
    			</c:otherwise>
    		</c:choose>
    	</table>
    	
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
    	
    	<input id="olympiad-id" type="hidden" value="${olympiad.getId ()}" />
    	<script src="/resources/js/olympiad.js"></script>
    </body>
</html>