window.onload = function (e) {
	var metas = document.getElementsByTagName ("meta");
	var token  = metas ["_csrf"].getAttribute ("content");
	var header = metas ["_csrf_header"].getAttribute ("content");
	
	var button = document.getElementById ("send-attempt");
	if (button) {
		button.onclick = function (e) {
			var req = new XMLHttpRequest ();
			req.open ("POST", "/api/create/olympiad/attempt", true);
			req.setRequestHeader (header, token);
			
			req.onreadystatechange = function () {
				if (req.readyState != 4) { return; }
				
				if (req.status != 200) {
					alert (req.statusText + " / " + req.responseText);
				} else if (confirm (req.responseText)) { 
					var answer = JSON.parse (req.responseText);
					if (!answer.error) { location.reload (); }
				}
			}
			
			var data = new FormData ();
			data.append ("olympiad", document.getElementById ("olympiad-id").value);
			
			var comment = document.getElementById ("attempt-comment-input").value;
			data.append ("comment", comment)
			
			var input = document.getElementById ("attempt-file-input");
			console.log (input.files);
			for (let i = 0; i < input.files.length; i += 1) {
				var file = input.files [i];
	            data.append ("file", file, file.name);
			}
			
			req.send (data);
		}
	}
}

var toggleResults = function (finalize) {
	var metas = document.getElementsByTagName ("meta");
	var token  = metas ["_csrf"].getAttribute ("content");
	var header = metas ["_csrf_header"].getAttribute ("content");
	
	var req = new XMLHttpRequest ();
	req.open ("POST", "/api/update/olympiad/toggle-results", true);
	req.setRequestHeader (header, token);
	
	req.onreadystatechange = function () {
		if (req.readyState != 4) { return; }
		
		if (req.status != 200) {
			alert (req.statusText + " / " + req.responseText);
		} else if (confirm (req.responseText)) { 
			var answer = JSON.parse (req.responseText);
			if (!answer.error) { location.reload (); }
		}
	}
	
	var data = new FormData ();
	data.append ("olympiad", document.getElementById ("olympiad-id").value);
	data.append ("finalize", finalize);
	
	req.send (data);
}

var saveResults = function () {
	var metas = document.getElementsByTagName ("meta");
	var token  = metas ["_csrf"].getAttribute ("content");
	var header = metas ["_csrf_header"].getAttribute ("content");
	
	var req = new XMLHttpRequest ();
	req.open ("POST", "/api/update/attempt/save-results", true);
	req.setRequestHeader (header, token);
	
	req.onreadystatechange = function () {
		if (req.readyState != 4) { return; }
		
		if (req.status != 200) {
			alert (req.statusText + " / " + req.responseText);
		} else if (confirm (req.responseText)) { 
			var answer = JSON.parse (req.responseText);
			if (!answer.error) { location.reload (); }
		}
	}
	
	var data = new FormData ();
	data.append ("attempt", document.getElementById ("attempt-id").value);
	
	var comments = document.getElementsByClassName ("attempt-problem-comment");
	var points = document.getElementsByClassName ("attempt-problem-points");
	var ids = document.getElementsByClassName ("attempt-problem-id");
	
	var results = [];
	for (var i = 0; i < ids.length; i++) {
		var comment = comments [i].value;
		var pointsI = points [i].value;
		var id      = ids [i].value;
		results.push ({
			"comment": comment,
			"points": pointsI,
			"id": id
		});
	}
	
	data.append ("results", JSON.stringify (results));
	req.send (data);
}