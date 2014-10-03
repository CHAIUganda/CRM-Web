var app = angular.module('omnitechApp', []);

app.controller('CustomContactCtrl', function ($scope, $http) {

    $scope.customerContacts = customerContacts;

    $scope.addContact = function(){
        $scope.customerContacts.push({})
    }

    $scope.deleteContact = function(index){
        $scope.customerContacts.splice(index,1)
    }

});

