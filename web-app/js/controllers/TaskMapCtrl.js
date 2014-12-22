///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var TaskMapCtrl = (function () {
            function TaskMapCtrl(scope, dataLoader, filterFilter) {
                this.scope = scope;
                this.dataLoader = dataLoader;
                this.filterFilter = filterFilter;
                this.mapC = new chai.MapContainer(chaiMapData, function (task, marker) {
                    console.log(task, marker);
                    scope.task = task;
                    scope.$apply();
                });
            }
            TaskMapCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', TaskMapCtrl];
            };
            return TaskMapCtrl;
        })();
        angular.module('omnitechApp', ['ngResource']).controller('TaskMapCtrl', TaskMapCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=TaskMapCtrl.js.map