app.controller('usersController', function($scope) {
    $scope.headingTitle = "User List";
});

app.controller('rolesController', function($scope) {
    $scope.headingTitle = "Roles List";
});

app.controller('clerkController', function($scope, Items) {
    Items.query(function(data) {
        $scope.itemslist = data;
    });
});

app.controller('fulfillmentController', function($scope, Items) {
    Items.query(function(data) {
        $scope.itemslist = data;
    });
});
