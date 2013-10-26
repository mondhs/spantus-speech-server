function GrammarUtilCtrl($scope, $http, $location) {

	$scope.graphemeWord = null;
	
	
	$scope.caseNoun = null;
	$scope.caseType = 'dative/singularis'

	$scope.numeralesNoun = null;
	$scope.numeralesNumber = null;
	
	$scope.resultGraphemePhonemes = null;

	$scope.transformToPhonemes = function() {
		$http.get('/api/grammar/lt/phonemes/' + $scope.graphemeWord + '/multiple').success(
				function(data) {
					$scope.graphemePhonemes = data;
					console.log("transformToPhonemes: " + data);
				});
	}
	
	$scope.transformCase = function() {
		$http.get('/api/grammar/lt/noun/'+$scope.caseNoun+'/case/'+$scope.caseType).success(
				function(data) {
					$scope.graphemeWord = data;
					console.log("transformCase: " + data + "; case type: " + $scope.caseType);
					$scope.transformToPhonemes();
				});
	}
	
	$scope.matchNumerales = function() {
		$http.get('/api/grammar/lt/noun/'+$scope.numeralesNoun+'/numerales/' + $scope.numeralesNumber).success(
				function(data) {
					$scope.graphemeWord = $scope.numeralesNumber + " " + data;
					console.log("matchNumerales: " + data);
					$scope.transformToPhonemes();
				});
	}
}

