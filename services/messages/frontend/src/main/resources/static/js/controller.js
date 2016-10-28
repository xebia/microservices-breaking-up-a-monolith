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
