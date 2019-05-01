window.onload = function (e) {
	var metas = document.getElementsByTagName ("meta");
	var token  = metas ["_csrf"].getAttribute ("content");
	var header = metas ["_csrf_header"].getAttribute ("content");
	
	var button = document.getElementById ("login");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/unchecked/login");
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
			data.append ("username", "admin");
			data.append ("password", "admin");
			data.append ("remember-me", "on");
			console.log (data);
			
			req.send (data);
		}
	}
}