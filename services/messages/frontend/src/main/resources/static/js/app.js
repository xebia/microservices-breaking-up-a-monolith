var app = angular.module('app', ['ngRoute','ngResource']);

app.config(function($routeProvider){
    $routeProvider
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
});

