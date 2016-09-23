'use strict';

/**
 * @ngdoc function
 * @name angularTest3App.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the angularTest3App
 */
angular.module('angularTest3App')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
