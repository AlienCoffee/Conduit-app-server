window.onload = function (e) {
	var metas = document.getElementsByTagName ("meta");
	var token  = metas ["_csrf"].getAttribute ("content");
	var header = metas ["_csrf_header"].getAttribute ("content");
	
	var button = document.getElementById ("logout");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/logout", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.responseText);
				} else if (confirm (req.statusText)) { 
					location.reload (); 
				}
			}
			
			req.send ();
		}
	}
	
	button = document.getElementById ("createPeriod");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/create/period", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.responseText);
				} else if (confirm (req.statusText)) { 
					location.reload (); 
				}
			}
			
			var data = new FormData ();
			data.append ("name", "Third period");
			data.append ("since", "18.04.2019 08:12:29");
			console.log (data);
			
			req.send (data);
		}
	}
	
	button = document.getElementById ("createGroup");
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
			data.append ("description", "--- // ---");
			data.append ("name", "Third group");
			data.append ("periodID", "0");
			console.log (data);
			
			req.send (data);
		}
	}
}