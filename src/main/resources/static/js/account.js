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
						container.innerHTML = users.message;
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
}