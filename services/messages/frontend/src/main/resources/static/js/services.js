app.service("Clerks", function($resource) {
    return $resource("http://localhost:8082/clerk/list");
})

app.service("Fulfillments", function($resource) {
    return $resource("http://localhost:8082/fulfillment/list");
})


