app.service("Clerks", function($resource) {
    return $resource("http://localhost:8082/clerk/list");
})

app.service("Fulfillments", function($resource, $http) {
        this.fetchFulfillments = function() { return $http.get('token').then(function(result) {
    		return $http({
    			url : '/fulfillment/list',
    			method : 'GET',
    			headers : {
    				'X-Auth-Token' : result.data.token
    			}
    		});
    	})
    	}

})


