'use strict';

/**
 * @ngdoc overview
 * @name angularTest3App
 * @description
 * # angularTest3App
 *
 * Main module of the application.
 */
angular
  .module('angularTest3App', [
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
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/main', {
              templateUrl: 'views/main.html',
              controller: 'MainCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl'
      })
      .when('/edit/:projectId', {
        controller: 'ItemCtrl',
        templateUrl: 'detail.html',
      })
      .when('/new', {
        controller: 'ItemCtrl',
        templateUrl: 'detail.html',
      })
      .otherwise({
        redirectTo: '/'
      });
  });
