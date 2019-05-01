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
        <title>Account</title>
    </head>
    
    <body>
    	<h2>Account</h2>
    	<a href="/">index</a>
    	
    	<p>
    		<span>Create user: </span>
    		<input id="cuLogin" type="text" placeholder="login" />
    		<input id="cuPhone" type="text" placeholder="phone" />
    		<input id="cuPassword" type="text" placeholder="password" />
    		<button id="cuButton">create</button>
    	</p>
    	
    	<p>
    		<div>
   				<span>Registered users:</span>
   				<button id="luButton">load</button>
   				<div id="usersDiv"></div>
			</div>
    	</p>
    	
    	<p>
    		<span>Create option: </span>
    		<input id="coName" type="text" placeholder="option name" />
    		<button id="coButton">create</button>
    	</p>
    	
    	<p>
    		<div>
   				<span>Registered options:</span>
   				<button id="loButton">load</button>
   				<div id="optionsDiv"></div>
			</div>
    	</p>
    	
    	<script src="/resources/js/account.js"></script>
    </body>
</html>