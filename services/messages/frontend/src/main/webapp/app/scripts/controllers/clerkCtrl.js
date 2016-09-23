'use strict';

/**
 * @ngdoc function
 * @name shop-app.controller:ClerkCtrl
 * @description
 * # ClerkCtrl
 * Controller of the shop-app
 */
angular.module('shop-app')

  .controller('ClerkCtrl', function($scope) {

    $scope.test = "HOI"
    $scope.user = {name: 'guest', last: 'visitor'};
  })
  ;
