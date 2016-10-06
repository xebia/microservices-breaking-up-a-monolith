app.service("Items", function($resource) {
    return $resource("http://localhost:8082/list");
})
