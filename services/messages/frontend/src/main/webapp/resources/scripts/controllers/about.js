'use strict';

/**
 * @ngdoc function
 * @name shop-app.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the shop-app
 */
angular.module('about')

  //.service('itemslist', function($http) {

  //  $http.get('http://localhost:8081/list').then(function(response){
  //      return response.data;
  //      }
  //  );

  //})

  .service("Items", function($resource) {
    return $resource("http://localhost:8081/list");
  })

  .controller('AboutCtrl', function($scope, Items) {

    $scope.text = "Hello Sir !";
    $scope.themethod = function(){
            return "WHOHOO";
    }

    Items.query(function(data) {
        $scope.itemslist = data;
      });

  })

  .controller('itemCtrl',
      function($location, $routeParams, projects) {
          var editProject = this;
          var projectId = $routeParams.projectId,
              projectIndex;

          editProject.projects = projects;
          projectIndex = editProject.projects.$indexFor(projectId);
          editProject.project = editProject.projects[projectIndex];

          editProject.destroy = function() {
              editProject.projects.$remove(editProject.project).then(function(data) {
                  $location.path('/');
              });
          };

          editProject.save = function() {
              editProject.projects.$save(editProject.project).then(function(data) {
                  $location.path('/');
              });
          };
  })
  ;

