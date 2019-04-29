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
        <title>Login into account</title>
        
        <!--<link rel="stylesheet" href="/resources/css/index.css" />-->
        <!--<link rel="shortcut icon" href="/resources/gfx/jiraf.png" type="image/x-icon">-->
    </head>
    
    <body>
    	<h2>Log in</h2>
    	<a href="/">index</a>
    	
    	<div>
   			<!-- 
    		<p><input type="text" id="username" /></p>
    		<p><input type="password" id="password" /></p>
    		<p><input type="text" id="remember-me" /></p>
    		-->
    		<p><button id="login">Login</button></p>
    	</div>
    	
    	<script type="text/javascript" src="/resources/js/login.js"></script>
    </body>
</html>