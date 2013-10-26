function CorpusListCtrl($scope, $http,  $location, $timeout, AudioService) {
	$scope.corpus = []
	,$scope.searchText = null
	,$scope.predicate = '-createdEntryOn'
	,$scope.requestPending = false
	,$scope.entriesCount=0;

	
	
	$scope.lazyRetrieveCorpus = function() {
		console.log("[lazyRetrieveCorpus] " );
		$http.get('/api/corpus').success(function(data) {
			$scope.corpus = data;
		});
		$http.get('/api/corpus/count').success(function(data) {
			$scope.entriesCount = data;
		});
	}
	
	var mytimeout = $timeout($scope.lazyRetrieveCorpus,500);

	

	$scope.setSelectedEntry = function(entry) {
		// entry.done = !entry.done;
		$location.path('/corpus/' + entry.id)
	}
	
	$scope.deleteRecord = function(entry){
		 var deleteEntry = confirm('Are you absolutely sure you want to delete '+entry.id+' ?');   

		 if (deleteEntry) {
				$http.delete('/api/corpus/entry/' + entry.id).success(function(data) {
					console.log("deleted: " + entry.id);
					$timeout($scope.lazyRetrieveCorpus,500);
				});
		 }
		
	};
	
	$scope.playRecord = function(entry){
		console.log("playRecord /api/corpus/entry/"+entry.id+"/stream");
		AudioService.play("/api/corpus/entry/"+entry.id+"/stream");
	};

	$scope.criteriaMatch = function() {
		console.log("[criteriaMatch] " + $scope.searchText);
		if($scope.requestPending == false){
			$scope.requestPending = true;
			$timeout($scope.retrieveInfo,1000);
		};

	};
	
	$scope.retrieveInfo = function() {
		console.log("[retrieveInfo] " + $scope.searchText);
		$http.get('/api/corpus/entries?searchText='+$scope.searchText).success(function(data) {
			$scope.corpus = data;
			$scope.requestPending = false;
		});
	}
	

}

