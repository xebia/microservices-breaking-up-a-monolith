app.controller('clerkController', function($scope, Clerks) {
    Clerks.query(function(data) {
        $scope.clerkslist = data;
    });
});

app.controller('fulfillmentController', function($scope, Fulfillments) {
    Fulfillments.query(function(data) {
        $scope.fulfillmentslist = data;
    });
});

app.controller('navigation',

    function($rootScope, $scope, $http, $location, $route) {

    	$scope.tab = function(route) {
    		return $route.current && route === $route.current.controller;
    	};

    	$http.get('user').success(function(data) {
    		if (data.userAuthentication.principal) {
    		    $scope.user = data.userAuthentication.principal;
    			$rootScope.authenticated = true;
    		} else {
    			$rootScope.authenticated = false;
    		}
    	}).error(function() {
    		$rootScope.authenticated = false;
    	});

    	$scope.credentials = {};

    	$scope.logout = function() {
    		$http.post('logout', {}).success(function() {
    			$rootScope.authenticated = false;
    			$location.path("/");
    		}).error(function(data) {
    			console.log("Logout failed")
    			$rootScope.authenticated = false;
    		});
    	}

});


