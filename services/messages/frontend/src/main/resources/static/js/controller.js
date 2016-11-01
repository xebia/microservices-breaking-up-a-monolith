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

     function($rootScope, $http, $location) {

         var self = this

         var authenticate = function(credentials, callback) {
           var headers = credentials ? {authorization : "Basic "
               + btoa(credentials.username + ":" + credentials.password)
           } : {};

           $http.get('user', {headers : headers}).then(function(response) {
             if (response.data.name) {
               $rootScope.authenticated = true;
             } else {
               $rootScope.authenticated = false;
             }
             callback && callback();
           }, function() {
             $rootScope.authenticated = false;
             callback && callback();
           });
         }

         authenticate();
         self.credentials = {};
         self.login = function() {
             authenticate(self.credentials, function() {
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
