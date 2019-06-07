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
    			<h3>Information groups</h3>
		    	
		    	<c:choose>
		    		<c:when test="${INFO_groups != null && not empty INFO_groups}">
		    			<c:forEach var="group" items="${INFO_groups}">
		    				<div>
				    			<p>(${group.getId ()}) <b>${group.getName ()}</b></p>
				    			<div><b>Description:</b> ${group.getDescription ()}</div>
				    			<div><b>Head teacher:</b> ${group.getHead ().getLogin ()}</div>
				   				<div>
				   					<b>Links:</b>
				   					<a href="/group/${group.getId ()}">info</a>
				   					<button>join</button>
				   				</div>
				    		</div>
		    			</c:forEach>
		    		</c:when>
		    		
		    		<c:otherwise>
		    			<p>No information groups in period yet</p>
		    		</c:otherwise>
		    	</c:choose>
		    	
		    	<h3>Study groups</h3>
		    	
		    	<c:choose>
		    		<c:when test="${STUDY_groups != null && not empty STUDY_groups}">
		    			<c:forEach var="group" items="${STUDY_groups}">
		    				<div>
				    			<p>(${group.getId ()}) <b>${group.getName ()}</b></p>
				    			<div><b>Description:</b> ${group.getDescription ()}</div>
				    			<div><b>Head teacher:</b> ${group.getHead ().getLogin ()}</div>
				   				<div>
				   					<b>Links:</b>
				   					<a href="/group/${group.getId ()}">info</a>
				   					<button>join</button>
				   				</div>
				    		</div>
		    			</c:forEach>
		    		</c:when>
		    		
		    		<c:otherwise>
		    			<p>No study groups in period yet</p>
		    		</c:otherwise>
		    	</c:choose>
		    	
		    	<h3>Elimination groups</h3>
		    	
		    	<c:choose>
		    		<c:when test="${ELIMINATION_groups != null && not empty ELIMINATION_groups}">
		    			<c:forEach var="group" items="${ELIMINATION_groups}">
		    				<div>
				    			<p>(${group.getId ()}) <b>${group.getName ()}</b></p>
				    			<div><b>Description:</b> ${group.getDescription ()}</div>
				    			<div><b>Head teacher:</b> ${group.getHead ().getLogin ()}</div>
				   				<div>
				   					<b>Links:</b>
				   					<a href="/group/${group.getId ()}">info</a>
				   					<button>join</button>
				   				</div>
				    		</div>
		    			</c:forEach>
		    		</c:when>
		    		
		    		<c:otherwise>
		    			<p>No elimination groups in period yet</p>
		    		</c:otherwise>
		    	</c:choose>
		    	
		    	<h3>Pool groups</h3>
		    	
		    	<c:choose>
		    		<c:when test="${POOL_groups != null && not empty POOL_groups}">
		    			<c:forEach var="group" items="${POOL_groups}">
		    				<div>
				    			<p>(${group.getId ()}) <b>${group.getName ()}</b></p>
				    			<div><b>Description:</b> ${group.getDescription ()}</div>
				    			<div><b>Head teacher:</b> ${group.getHead ().getLogin ()}</div>
				   				<div>
				   					<b>Links:</b>
				   					<a href="/group/${group.getId ()}">info</a>
				   					<button>join</button>
				   				</div>
				    		</div>
		    			</c:forEach>
		    		</c:when>
		    		
		    		<c:otherwise>
		    			<p>No pool groups in period yet</p>
		    		</c:otherwise>
		    	</c:choose>
    		</c:when>
    		
    		<c:otherwise>
    			<p>You don't have rights to see groups list</p>
    		</c:otherwise>
    	</c:choose>
    	
    	<script src="/resources/js/period.js"></script>
    </body>
</html>