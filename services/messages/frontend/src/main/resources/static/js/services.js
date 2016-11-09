app.service("Clerks", function($resource) {
    return $resource("http://localhost:8082/clerk/list");
})

app.service("Fulfillments", function($resource, $http) {

    var token= $resource("http://localhost:8082/token").get;

    var res = $resource('http://localhost:8083/fulfillment/list', {}, {
        get: {
              method : 'GET',
              isArray:false,
              headers : {'X-Auth-Token' : token}
            },
            });
    return res;
})
