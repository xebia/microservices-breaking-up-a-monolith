app.controller('clerkController', function($scope, Clerks) {
    Clerks.query(function(data) {
        $scope.clerkslist = data;
    });
});

app.controller('fulfillmentController', function($scope, Fulfillments, $http) {
    Fulfillments.fetchFulfillments().then(function(result) {
        $scope.fulfillmentslist = result.data;
    });
});

app.controller('navigation',

     function($rootScope, $http, $location) {
         var self = this

         var authenticate = function(credentials) {
           var headers = credentials ? {authorization : "Basic "
               + btoa(credentials.username + ":" + credentials.password)
           } : {};

           return $http.get('user', {headers : headers}).then(function(response) {
             if (response.data.name) {
               $rootScope.authenticated = true;
             } else {
               $rootScope.authenticated = false;
             }
           }, function() {
             $rootScope.authenticated = false;
           });
         }

         authenticate();
         self.credentials = {};
         self.login = function() {
             authenticate (self.credentials).then(function() {
               if ($rootScope.authenticated) {
                 $location.path("/");
                 self.error = false;
                 $rootScope.authenticated = true;
               } else {
                 $location.path("/login");
                 self.error = true;
                 $rootScope.authenticated = false;
               }
             });
         };

         self.logout = function() {
            $http.post('logout', {}).finally(function() {
                $rootScope.authenticated = false;
                $location.path("/");
            });
         };

   });
