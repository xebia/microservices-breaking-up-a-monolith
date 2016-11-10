dapp.service("Clerks", function($resource) {
    return $resource("http://localhost:8082/clerk/list");
})

app.service("Fulfillments", function($http) {
    $http.get('token').success(function(token) {
		$http({
			url : 'http://localhost:8083/fulfillment/list',
			method : 'GET',
			headers : {
				'X-Auth-Token' : token.token
			}
		}).success(function(data) {
           return data
		});
	})

//    return $resource($http.get('token')
//    .then(function(response) {
//            $http({
//              url : 'http://localhost:8083/fulfillment/list',
//              method : 'GET',
//              headers : {
//                'X-Auth-Token' : response.data.token
//              }
//            });
//        }));
})
