window.onload = function (e) {
	var metas = document.getElementsByTagName ("meta");
	var token  = metas ["_csrf"].getAttribute ("content");
	var header = metas ["_csrf_header"].getAttribute ("content");
	
	var button = document.getElementById ("cuButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/unchecked/create/user", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("login",    document.getElementById ("cuLogin").value);
			data.append ("phone",    document.getElementById ("cuPhone").value);
			data.append ("password", document.getElementById ("cuPassword").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("luButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("GET", "/api/get/users", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var users = JSON.parse (req.responseText);
					
					var container = document.getElementById ("usersDiv");
					container.innerHTML = "";
					
					if (users.error) {
						container.innerHTML = users.message;
					} else {
						users.object.forEach (user => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (user);
							container.append (elem);
						});
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("coButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/create/option", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("name", document.getElementById ("coName").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("loButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("GET", "/api/get/options", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var options = JSON.parse (req.responseText);
					
					var container = document.getElementById ("optionsDiv");
					container.innerHTML = "";
					
					if (options.error) {
						container.innerHTML = options.message;
					} else {
						options.object.forEach (option => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (option);
							container.append (elem);
						});
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("lmButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("GET", "/api/get/methods", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("methodsDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						methods.object.forEach (method => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (method);
							container.append (elem);
						});
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("amrButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/update/add/method-rule", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("method",   document.getElementById ("amrMethod").value);
			data.append ("option", document.getElementById ("amrOption").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("rmrButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/update/remove/method-rule", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("method",   document.getElementById ("rmrMethod").value);
			data.append ("option", document.getElementById ("rmrOption").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lrButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("GET", "/api/get/guard-rules", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("rulesDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						methods.object.forEach (method => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (method);
							container.append (elem);
						});
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("crButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/create/role", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("name",     document.getElementById ("crName").value);
			data.append ("template", document.getElementById ("crTemplate").value.toUpperCase ());
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lroButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("GET", "/api/get/roles", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("rolesDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						methods.object.forEach (method => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (method);
							container.append (elem);
						});
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("aroButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/update/add/role-option", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("role",   document.getElementById ("aroRole").value);
			data.append ("option", document.getElementById ("aroOption").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("rroButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/update/remove/role-option", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("role",   document.getElementById ("rroRole").value);
			data.append ("option", document.getElementById ("rroOption").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("cpButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/create/period", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("name",  document.getElementById ("cpName").value);
			
			var date = new Date (document.getElementById ("cpSince").value).toISOString ();
			data.append ("since", date.substring (0, date.length - 1));
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lpButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("GET", "/api/get/periods", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("periodsDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						methods.object.forEach (method => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (method);
							container.append (elem);
						});
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("artuButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/update/add/role-to-user", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("user",   document.getElementById ("artuUser").value);
			data.append ("period", document.getElementById ("artuPeriod").value);
			data.append ("role",   document.getElementById ("artuRole").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("rrtuButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/update/remove/role-from-user", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("user",   document.getElementById ("rrtuUser").value);
			data.append ("period", document.getElementById ("rrtuPeriod").value);
			data.append ("role",   document.getElementById ("rrtuRole").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lprtButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("GET", "/api/get/period/register-roles", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("templatesDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						/*
						methods.object.forEach (method => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (method);
							container.append (elem);
						});
						*/
						Object.keys (methods.object).forEach (key => {
							var elem = document.createElement ("div");
							elem.innerHTML = key + " / " + JSON.stringify (methods.object [key]);
							container.append (elem);
						});
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("lprButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/get/period/registered", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("registeredDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						Object.keys (methods.object).forEach (key => {
							var elem = document.createElement ("div");
							elem.innerHTML = key + " / " + JSON.stringify (methods.object [key]);
							container.append (elem);
						});
					}
				}
			}
			
			var data = new FormData ();
			data.append ("period", document.getElementById ("lprPeriod").value);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lpdButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/get/personal-data", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("personalDataDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						container.innerHTML = JSON.stringify (methods.object.values);
					}
				}
			}
			
			var data = new FormData ();
			data.append ("user",   document.getElementById ("lpdUser").value);
			data.append ("period", document.getElementById ("lpdPeriod").value);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lgtButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/get/group/types", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var methods = JSON.parse (req.responseText);
					
					var container = document.getElementById ("groupTypesDiv");
					container.innerHTML = "";
					
					if (methods.error) {
						container.innerHTML = methods.message;
					} else {
						container.innerHTML = JSON.stringify (methods.object);
					}
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("cgButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/create/group", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("name",   document.getElementById ("cgName").value);
			data.append ("period", document.getElementById ("cgPeriod").value);
			data.append ("type",   document.getElementById ("cgType").value.toUpperCase ());
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lpgButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/get/period/groups", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var groups = JSON.parse (req.responseText);
					
					var container = document.getElementById ("groupsDiv");
					container.innerHTML = "";
					
					if (groups.error) {
						container.innerHTML = groups.message;
					} else {
						groups.object.forEach (group => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (group);
							container.append (elem);
						});
					}
				}
			}
			
			var data = new FormData ();
			data.append ("period", document.getElementById ("lpgPeriod").value);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("agButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/create/group-assignment", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("user",    document.getElementById ("agUser").value);
			data.append ("group",   document.getElementById ("agGroup").value);
			data.append ("status",  document.getElementById ("agStatus").value.toUpperCase ());
			data.append ("role",    document.getElementById ("agRole").value.toUpperCase ());
			data.append ("comment", document.getElementById ("agComment").value);
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("lgaButton");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/get/group/members", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText);
				} else if (confirm (req.responseText)) { 
					var groups = JSON.parse (req.responseText);
					
					var container = document.getElementById ("groupMembersDiv");
					container.innerHTML = "";
					
					if (groups.error) {
						container.innerHTML = groups.message;
					} else {
						groups.object.forEach (group => {
							var elem = document.createElement ("div");
							elem.innerHTML = JSON.stringify (group);
							container.append (elem);
						});
					}
				}
			}
			
			var data = new FormData ();
			data.append ("group", document.getElementById ("lgaGroup").value);
			
			req.send (data);
		}
	}
}