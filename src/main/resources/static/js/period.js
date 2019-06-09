var userData = {};

window.onload = function (e) {
	var metas = document.getElementsByTagName ("meta");
	var token  = metas ["_csrf"].getAttribute ("content");
	var header = metas ["_csrf_header"].getAttribute ("content");
	
	var button = document.getElementById ("loadTypes");
	var template = null;
	var templates = {};
	
	if (button) {
		button.onclick = function (e) {
			setTimeout (function () {
				var req = new XMLHttpRequest ();
				req.open ("GET", "/api/get/period/register-roles", true);
				req.setRequestHeader (header, token);
				
				req.onreadystatechange = function () {
					if (req.readyState != 4) { return; }
					
					if (req.status != 200) {
						alert (req.statusText + " / " + req.responseText);
					} else if (confirm (req.responseText)) { 
						var answer = JSON.parse (req.responseText);
						
						var container = document.getElementById ("regType");
						container.innerHTML = "";
						
						if (answer.error) {
							container.innerHTML = answer.message;
						} else {
							templates = answer.object;
							
							Object.keys (templates).forEach (temp => {
								var elem = document.createElement ("option");
								elem.setAttribute ("value", temp);
								elem.innerHTML = temp;
								
								container.append (elem);
							});
						}
					}
				}
				
				req.send ();
			}, 200);
			
			var req2 = new XMLHttpRequest ();
			req2.open ("POST", "/api/get/personal-data", true);
			req2.setRequestHeader (header, token);
			
			req2.onreadystatechange = function () {
				if (req2.readyState != 4) { return; }
				
				if (req2.status != 200) {
					alert (req.statusText + " / " + req.responseText);
				} else if (confirm (req2.responseText)) { 
					var answer = JSON.parse (req2.responseText);
					
					var container = document.getElementById ("regDiv");
					container.innerHTML = "";
					
					if (answer.error) {
						container.innerHTML = answer.message;
					} else {
						userData = answer.object.values;
					}
				}
			}
			
			var data = new FormData ();
			data.append ("period", document.getElementById ("periodID").value);
			data.append ("user", document.getElementById ("userID").value);
			
			req2.send (data);
		}
	}
	
	var select = document.getElementById ("regType");
	if (select) {
		select.onchange = function (e) {
			template = templates [select.value];
			
			var container = document.getElementById ("regDiv");
			container.innerHTML = "";
			
			template.forEach (row => {
				if (row.rowType == "title") {
					var elem = document.createElement ("h3");
					elem.innerHTML = row.title;
					container.append (elem);
				} else if (row.rowType == "field") {
					var div = document.createElement ("div");
					container.append (div);
					
					var p = document.createElement ("p");
					p.innerHTML = row.title + (row.required ? "*" : "") 
								+ (row.comment ? "<br />" + row.comment : "");
					div.append (p);
					
					if (row.type == "TEXT") {
						var p2 = document.createElement ("p");
						div.append (p2);
						
						var input = document.createElement ("input");
						input.setAttribute ("id", row.parameterName);
						input.value = userData [row.parameterName]
									? userData [row.parameterName]
									: "";
						p2.append (input);
					} else if (row.type == "NUMBER") {
						var p2 = document.createElement ("p");
						div.append (p2);
						
						var input = document.createElement ("input");
						input.setAttribute ("id", row.parameterName);
						input.value = userData [row.parameterName]
									? userData [row.parameterName]
									: "";
						input.setAttribute ("type", "number");
						p2.append (input);
					} else if (row.type == "DATE") {
						var p2 = document.createElement ("p");
						div.append (p2);
						
						var input = document.createElement ("input");
						input.setAttribute ("id", row.parameterName);
						input.value = userData [row.parameterName]
									? userData [row.parameterName]
									: "";
						input.setAttribute ("type", "date");
						p2.append (input);
					}
				}
			});
			
			var save = document.createElement ("button");
			save.setAttribute ("id", "saveData");
			save.innerHTML = "save";
			container.append (save);
			
			save.onclick = function (e) {
				var req = new XMLHttpRequest ();
				req.open ("POST", "/api/create/period-registration", true);
				req.setRequestHeader (header, token);
				
				req.onreadystatechange = function () {
					if (req.readyState != 4) { return; }
					
					if (req.status != 200) {
						alert (req.statusText + " / " + req.responseText);
					} else if (confirm (req.responseText)) { 
						location.reload (); 
					}
				}
				
				var data = new FormData ();
				data.append ("template", select.value);
				
				var collector = {};
				template.forEach (row => {
					if (row.rowType == "field") {
						var elem = document.getElementById (row.parameterName);
						if (elem.value) {
							//collector [row.parameterName] = elem.value;
							data.append (row.parameterName, elem.value);
						}
					}
				});
				data.append ("period", document.getElementById ("periodID").value);
				
				req.send (data);
			}
		}
	}
	
	for (let button of document.getElementsByClassName ("group-join-button")) {
		button.onclick = function (e) {
			setTimeout (function () {
				var req = new XMLHttpRequest ();
				req.open ("POST", "/api/create/group-join", true);
				req.setRequestHeader (header, token);
				
				req.onreadystatechange = function () {
					if (req.readyState != 4) { return; }
					
					if (req.status != 200) {
						alert (req.statusText + " / " + req.responseText);
					} else if (confirm (req.responseText)) { 
						var answer = JSON.parse (req.responseText);
						
						if (answer.error) {
							alert (answer.message);
						} else {
							location.reload ();
						}
					}
				}
				
				var data = new FormData ();
				data.append ("group", button.id.substr (4));
				
				req.send (data);
			}, 200);
		}
	}
}