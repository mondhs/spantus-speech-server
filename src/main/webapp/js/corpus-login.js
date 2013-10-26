function CorpusLoginCtrl($scope,$http,$location) {
	$scope.user = {userName:"", password:""};
	$scope.loginUser = function(){
		$http({
			url : '/api/user/login',
			method : "POST",
			data : "userName="+$scope.user.userName + "&password=" +$scope.user.password,
			headers : {
				'Content-Type' : 'application/json'
			}
		}).success(function(data, status, headers, config) {
			$location.path("/");
		}).error(function(data, status, headers, config) {
			alert(status + ' ' + headers);
		});
	}
}

