app.factory('AudioService',
		function($document) {
			var audioElement = $document[0].createElement('audio'); // <-- Magic
			// trick
			// here
			return {
				audioElement : audioElement,

				load : function(filename) {
					existingAudioElement = $document[0]
							.getElementById("audioPlayback");
					console.log("load " + filename);
					existingAudioElement.src = filename;
					existingAudioElement.load();
				},

				play : function(filename) {
					console.log("play " + filename);
					audioElement.src = filename;
					audioElement.play(); // <-- Thats all you need
				}
			// Exersise for the reader - extend this service to include other
			// functions
			// like pausing, etc, etc.

			}
		});
/*
app.factory("user", function() {
	return {
		user : {}
	}
});

app.service("UserService", function($http, $location, user) {
	return {
		isAuthenticated : function() {
			return localStorage.getItem("authToken") != undefined;
		},

		getAuthToken : function() {
			return localStorage.getItem("authToken");
		},

		setUser : function(u) {
			user.user = u;
			localStorage.setItem("authToken", u.authToken);
		},

		endSession : function() {
			localStorage.removeItem("authToken");
			$location.path("/login");
		}
	}
});
*/