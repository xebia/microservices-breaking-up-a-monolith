'use strict';

/**
 * @ngdoc overview
 * @name shop-app
 * @description
 * # shop-app
 *
 * Main module of the application.
 */
angular
  .module('shop-app', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/clerk.html',
        controller: 'ClerkCtrl'
      })
      .when('/clerk', {
              templateUrl: 'views/clerk.html',
              controller: 'ClerkCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
