var app = angular.module('app', ['ngRoute','ngResource']);

app.config(function($routeProvider, $httpProvider){
    $routeProvider
        .when('/', {
               templateUrl : 'home.html'
        })
        .when('/clerk',{
            templateUrl: '/views/clerk.html',
            controller: 'clerkController'
        })
        .when('/fulfillment',{
            templateUrl: '/views/fulfillment.html',
            controller: 'fulfillmentController'
        })
        .when('/login', {
                 templateUrl : 'login.html',
                 controller : 'navigation',
                 controllerAs: 'controller'
               })
        .otherwise(
            { redirectTo: '/'}
        );
        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
});

