'use strict';
var app = angular.module('CorpusApp',
		[ 'ui.bootstrap', 'ngRoute', 'analytics' ]).config(
		[ 'AnalyticsProvider', function(AnalyticsProvider) {
			AnalyticsProvider.account = 'UA-44259253-2';

		} ]).config([ '$routeProvider', function($routeProvider, $rootScope) {
	$routeProvider.when('/corpus', {
		templateUrl : 'templates/corpus-list.html',
		controller : 'CorpusListCtrl',
		pageTitle : 'List'
	}).when('/corpus/:itemId', {
		templateUrl : 'templates/corpus-detail.html',
		controller : CorpusDetailCtrl,
		pageTitle : 'Detail'
	}).when('/login', {
		templateUrl : 'templates/corpus-login.html',
		controller : CorpusLoginCtrl,
		pageTitle : 'Login'
	}).when('/record', {
		templateUrl : 'templates/corpus-record.html',
		controller : CorpusRecordCtrl,
		pageTitle : 'Record'
	}).when('/grammar', {
		templateUrl : 'templates/grammar-util.html',
		controller : GrammarUtilCtrl,
		pageTitle : 'Grammar'
	}).when('/recognition', {
		templateUrl : 'templates/corpus-recognition.html',
		controller : CorpusRecognitionCtrl,
		pageTitle : 'Recognition'
	}).otherwise({
		redirectTo : '/corpus',
		pageTitle : 'Navigating'
	});
} ]).run([ '$location', '$rootScope', function($location, $rootScope) {
	$rootScope.$on('$routeChangeSuccess', function(event, current, previous) {
		if (current.$$route != null) {
			console.log("pageTitle: " + current.$$route.pageTitle);
			$rootScope.pageTitle = current.$$route.pageTitle;
		}
	});
} ]);

app.factory('myHttpResponseInterceptor',['$q','$location',function ($q, $location) {
    return {
        'response': function (response) {
            // Will only be called for HTTP up to 300
            console.log("RESPONSE: " + response.status);
            return response;
        },
        'responseError': function (rejection) {
        	console.log("ERROR: " + rejection.status);
            if(rejection.status === 401) {
            	$location.path("/login");
            }else if(rejection.status === 403) {
            	console.log("ERROR: " + rejection.status);
            }else if(rejection.status === 500) {
            	console.log("ERROR: " + rejection.status);
            }

            return $q.reject(rejection);
        }
    };
}]);


app.config(['$httpProvider', function ($httpProvider) {
	$httpProvider.interceptors.push('myHttpResponseInterceptor');
}]);

/**
 * 
 * @param $scope
 * @param $location
 * @param $timeout
 */
function CorpusGlobalCtrl($scope, $location, $http) {
	$scope.globalUser = {};
	$scope.globalLocation = $location;
	
	$scope.$on('$routeChangeSuccess', function() {
		$http.get('/api/user/userName').success(function(data) {
			console.log("[CorpusGlobalCtrl] user ", data);
			$scope.globalUser.userName = data;
		});
	});
	

	
	$scope.globalLogout = function() {
		$http.get('/api/user/logout').success(function(data) {
			$scope.globalUser = null;
		});
	}
	
	$scope.isGlobalLocationPath = function(path) {
		return $scope.globalLocation.path() == path;
	}
	$scope.startsGlobalLocationPath = function(path) {
		return $scope.globalLocation.path().lastIndexOf(path, 0) === 0;
	}

}
