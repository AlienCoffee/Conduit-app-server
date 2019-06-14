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
        <title>Group</title>
        
        <!--<link rel="stylesheet" href="/resources/css/index.css" />-->
        <!--<link rel="shortcut icon" href="/resources/gfx/jiraf.png" type="image/x-icon">-->
    </head>
    
    <body>
    	<h2>Group : ${group.getName ()}</h2>
    	
    	<a href="/">index</a>
    	<a href="/periods">periods</a>
    	<a href="/period/${period.getId ()}">period</a>    	
    	
    	<c:set var="gType" value="${group.getType ()}" />
    	
    	<p><b>Group description</b> : ${group.getDescription ()}</p>
    	<p><b>Group type</b> : ${gType}</p>
    	<p><b>Group head</b> : ${group.getHead ().getLogin ()}</p>
    
    	<c:if test="${not (gType eq 'POOL')}">
    		<h3>Information posts</h3>
    		
	    	<c:choose>
	    		<c:when test="${posts != null && not empty posts}">
	    			<c:forEach var="post" items="${posts}">
	    				<div>
			    			<p>(${post.getId ()}) <b>${post.getTitle ()}</b></p>
			    			<pre>${post.getContent ()}</pre>
			    			<div><b>Author:</b> ${post.getCommitter ().getLogin ()}</div>
			    			<div><b>Attachments:</b> ${post.getAttachments ().size ()}</div>
			    		</div>
	    			</c:forEach>
	    		</c:when>
	    		
	    		<c:otherwise>
	    			<p>No information posts in group</p>
	    		</c:otherwise>
	    	</c:choose>
    	</c:if>	
    	
    	<c:if test="${not ((gType eq 'POOL') or (gType eq 'INFO'))}">
    		<h3>Olympiads</h3>
    		
	    	<c:choose>
	    		<c:when test="${olympiads != null && not empty olympiads}">
	    			<c:forEach var="olympiad" items="${olympiads}">
	    				<div>
			    			<p>(${olympiad.getId ()}) <b>${olympiad.getName ()}</b></p>
			    			<pre>${olympiad.getDescription ()}</pre>
			    			<div><b>Begin:</b> ${olympiad.getPublished ()}</div>
			    			<div><b>Finish:</b> ${olympiad.getFinished ()}</div>
			    			<div><b>Author:</b> ${olympiad.getCommitter ().getLogin ()}</div>
			    			<div>
			   					<b>Links:</b>
			   					<a href="/olympiad/${olympiad.getId ()}">info</a>
			   				</div>
			    		</div>
	    			</c:forEach>
	    		</c:when>
	    		
	    		<c:otherwise>
	    			<p>No olympiads in group</p>
	    		</c:otherwise>
	    	</c:choose>
    	</c:if>	
    	
    	<h3>Members</h3>
    	
    	<c:choose>
    		<c:when test="${members != null && not empty members}">
    			<c:forEach var="member" items="${members}">
    				<div>
		    			<p>
		    				(${member.getUser ().getId ()}) 
		    				<b>${member.getUser ().getLogin ()}</b> --
		    				${member.getRole ().getTemplate ()}
	    				</p>
		    		</div>
    			</c:forEach>
    		</c:when>
    		
    		<c:otherwise>
    			<p>No members in group</p>
    		</c:otherwise>
    	</c:choose>
    	
    	<script src="/resources/js/period.js"></script>
    </body>
</html>