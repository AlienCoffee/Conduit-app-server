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
    	
    	<h3>User</h3>
    	
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
    	
    	<h3>Option</h3>
    	
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
    	
    	<p>
    		<div>
   				<span>Protected methods:</span>
   				<button id="lmButton">load</button>
   				<div id="methodsDiv"></div>
			</div>
    	</p>
    	
    	<p>
    		<span>Add method rule: </span>
    		<input id="amrMethod" type="text" placeholder="method name" />
    		<input id="amrOption" type="text" placeholder="option id" />
    		<button id="amrButton">add</button>
    	</p>
    	
    	<p>
    		<span>Remove method rule: </span>
    		<input id="rmrMethod" type="text" placeholder="method name" />
    		<input id="rmrOption" type="text" placeholder="option id" />
    		<button id="rmrButton">remove</button>
    	</p>
    	
    	<p>
    		<div>
   				<span>Current methods guard rules:</span>
   				<button id="lrButton">load</button>
   				<div id="rulesDiv"></div>
			</div>
    	</p>
    	
    	<h3>Role</h3>
    	
    	<p>
    		<span>Create role: </span>
    		<input id="crName" type="text" placeholder="role name" />
    		<button id="crButton">create</button>
    	</p>
    	
    	<p>
    		<div>
   				<span>Registered roles:</span>
   				<button id="lroButton">load</button>
   				<div id="rolesDiv"></div>
			</div>
    	</p>
    	
    	<p>
    		<span>Add role option: </span>
    		<input id="aroRole" type="text" placeholder="role id" />
    		<input id="aroOption" type="text" placeholder="option id" />
    		<button id="aroButton">add</button>
    	</p>
    	
    	<p>
    		<span>Remove role option: </span>
    		<input id="rroRole" type="text" placeholder="role id" />
    		<input id="rroOption" type="text" placeholder="option id" />
    		<button id="rroButton">remove</button>
    	</p>
    	
    	<h3>Period</h3>
    	
    	<p>
    		<span>Create period: </span>
    		<input id="cpName" type="text" placeholder="period name" />
    		<input id="cpSince" type="date" />
    		<input id="cpStatus" type="text" placeholder="status" />
    		<button id="cpButton">create</button>
    	</p>
    	
    	<p>
    		<div>
   				<span>Registered periods:</span>
   				<button id="lpButton">load</button>
   				<div id="periodsDiv"></div>
			</div>
    	</p>
    	
    	<h3>Role assignment</h3>
    	
    	<p>
    		<span>Assign role to user: </span>
    		<input id="artuUser" type="text" placeholder="user id" />
    		<input id="artuPeriod" type="text" placeholder="period id" />
    		<input id="artuRole" type="text" placeholder="role id" />
    		<button id="artuButton">assign</button>
    	</p>
    	
    	<p>
    		<span>Remove role from user: </span>
    		<input id="rrtuUser" type="text" placeholder="user id" />
    		<input id="rrtuPeriod" type="text" placeholder="period id" />
    		<input id="rrtuRole" type="text" placeholder="role id" />
    		<button id="rrtuButton">remove</button>
    	</p>
    	
    	<script src="/resources/js/account.js"></script>
    </body>
</html>