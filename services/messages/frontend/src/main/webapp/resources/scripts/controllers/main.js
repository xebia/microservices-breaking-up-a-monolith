'use strict';

/**
 * @ngdoc function
 * @name shop-app.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the shop-app
 */
angular.module('main')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
