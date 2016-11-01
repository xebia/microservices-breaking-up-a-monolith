app.service("Clerks", function($resource) {
    return $resource("http://localhost:8082/clerk/list");
})

app.service("Fulfillments", function($resource, $http) {
    return $resource($http.get('token')
    .then(function(response) {
            $http({
              url : 'http://localhost:8083/fulfillment/list',
              method : 'GET',
              headers : {
                'X-Auth-Token' : response.data.token
              }
            });
        }));
})
