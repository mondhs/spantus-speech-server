// /////////////////////////////// CorpusDetailCtrl
function CorpusDetailCtrl($scope, $routeParams, $location, $http, AudioService) {
	$scope.entry = {};
	$scope.recognitionResult = null;
	$scope.availableGrammas = null;
	$scope.grammaSelected = null;
	$scope.message = "";
	

	$http.get('/api/corpus/entry/' + $routeParams.itemId).success(function(data) {
		$scope.entry = data;
		$scope.grammaSelected = $scope.entry.grammar;
		AudioService.load("/api/corpus/entry/"+$routeParams.itemId+"/stream");
		console.log($scope.entry.fileName);
	});
	
	$http.get('/api/recognize/grammas').success(function(data) {
		$scope.availableGrammas = data;
	});
	

	$scope.updateEntry = function() {
		$http({
			url : '/api/corpus/entry/'+$routeParams.itemId,
			method : "POST",
			data : JSON.stringify($scope.entry),
			headers : {
				'Content-Type' : 'application/json'
			}
		}).success(function(data, status, headers, config) {
			 $location.path("/");
		}).error(function(data, status, headers, config) {
			alert(status + ' ' + headers);
		});

		$scope.entryText = '';
	};
	$scope.recognizeSound = function(){
		$scope.message = "requesting server to process: "+$scope.entry.fileName;
		$http.get('/api/recognize/entry/' + $scope.entry.id +"/"+$scope.grammaSelected).success(function(data) {
			$scope.entry.autoTranscript = data.result;
			$scope.entry.grammar = $scope.grammaSelected;
			$scope.recognitionResult = data.resultItems;
			$scope.message = "Found entries: "+data.resultItems.length;
			console.log("retrieved: "+data.resultItems.length);
		}).error(function(data, status, headers, config) {
			alert(status + ' ' + headers);
		});
		
	}
	$scope.updateManualTranscript = function(manualTranscript){
		$scope.entry.manualTranscript = manualTranscript;
	}
	
	$scope.deleteRecord = function(){
		 var deleteEntry = confirm('Are you absolutely sure you want to delete?');   

		 if (deleteEntry) {
				$http.delete('/api/corpus/entry/' + $routeParams.itemId).success(function(data) {
					console.log("deleted: " + $routeParams.itemId)
					$location.path("/");
				});
		 }
		
	};
	
	$scope.playRecord = function(){
		console.log("playRecord /api/corpus/entry/"+$scope.entry.id+"/stream");
		AudioService.play("/api/corpus/entry/"+$scope.entry.id+"/stream");
	};
}
