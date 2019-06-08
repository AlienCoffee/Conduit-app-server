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
        <title>Period</title>
        
        <!--<link rel="stylesheet" href="/resources/css/index.css" />-->
        <!--<link rel="shortcut icon" href="/resources/gfx/jiraf.png" type="image/x-icon">-->
    </head>
    
    <body>
    	<h2>Period : ${period.getName ()}</h2>
    	
    	<a href="/">index</a>
    	<a href="/periods">periods</a>
    	
    	<c:choose>
    		<c:when test="${have_assigned_roles}">
    			<p>
	    			<span><b>Assigned roles:</b></span>
	    			<c:forEach var="role" items="${role_applications}">
	    				<span>
	    					${role.getTemplate ().getName ()}
	    					(${role.getStatus ().name ()
	    						   .toLowerCase ()})
    					</span>
	    			</c:forEach>
	    		</p>
    		</c:when>
    		
    		<c:otherwise>
    			<p>
	    			<span>You have no <b>assigned</b> roles in this period</span>
	    		</p>
	    		<p>
					You also can
					<a href="/period/${period.getId ()}/registration">register</a>
					for roles
				</p>
    		</c:otherwise>
    	</c:choose>
    	
    	<c:choose>
    		<c:when test="${have_access_to_groups}">
    			<c:forEach var="type" items="${group_types}">
    				<h3>${type.getName ()} groups</h3>
    				
    				<c:set var="grous_list" value="${groups.get (type)}" />
    				
    				<c:choose>
			    		<c:when test="${grous_list != null && not empty grous_list}">
			    			<c:forEach var="group" items="${grous_list}">
			    				<div>
					    			<p>(${group.getGroup ().getId ()}) <b>${group.getGroup ().getName ()}</b></p>
					    			<div><b>Description:</b> ${group.getGroup ().getDescription ()}</div>
					    			<div><b>Head teacher:</b> ${group.getGroup ().getHead ().getLogin ()}</div>
					    			<div><b>You are in group:</b> ${group.getStatus () eq 'ASSIGNED' ? 'yes' : 'no'}</div>
					   				<div>
					   					<b>Links:</b>
					   					<a href="/group/${group.getGroup ().getId ()}">info</a>
					   					<c:if test="${group.getStatus () eq 'REJECTED'}">
					   						<button id="join${group.getGroup ().getId ()}" class="group-join-button">join</button>
					   					</c:if>
					   				</div>
					    		</div>
			    			</c:forEach>
			    		</c:when>
			    		
			    		<c:otherwise>
			    			<p>No information groups in period yet</p>
			    		</c:otherwise>
			    	</c:choose>
    			</c:forEach>
    		</c:when>
    		
    		<c:otherwise>
    			<p>You don't have rights to see groups list</p>
    		</c:otherwise>
    	</c:choose>
    	
    	<script src="/resources/js/period.js"></script>
    </body>
</html>