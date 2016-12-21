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
        .otherwise(
            { redirectTo: '/'}
        );
        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
});

