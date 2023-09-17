function loginCallback(data) {
	var xhr = new XMLHttpRequest();
	xhr.onload = function() {
		if(xhr.status == 200) {
			showAccessResult(true)
			console.log('Signed in as: ' + xhr.responseText);
		} else {
			showAccessResult(false)
		}
	};
	xhr.open('POST', '/api/v1/users');
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xhr.send('idToken=' + data.credential);
}