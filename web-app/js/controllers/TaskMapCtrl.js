///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var TaskMapCtrl = (function () {
            function TaskMapCtrl(scope, dataLoader, filterFilter) {
                var _this = this;
                this.scope = scope;
                this.dataLoader = dataLoader;
                this.filterFilter = filterFilter;
                this.mapC = new chai.MapContainer(chaiMapData, function (task, marker) {
                    scope.task = task;
                    scope.marker = marker;
                    scope.$apply();
                });
                scope.momentFromNow = function () {
                    if (!scope.task || !scope.task.dueDate)
                        return "";
                    //2014-12-31T08:19:17Z
                    //'MMMM Do YYYY, h:mm:ss a
                    return moment(scope.task.dueDate).fromNow();
                };
                $('#dueDateText').datepicker().on('changeDate', function (ev) {
                    _this.updateTaskDate(ev.date);
                    scope.$apply();
                });
            }
            TaskMapCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', TaskMapCtrl];
            };
            TaskMapCtrl.prototype.updateTaskDate = function (date) {
                var task = this.scope.task;
                task.dueDate = date;
                task.dueDays = chai.Utils.dayDiff(new Date(), date);
                this.scope.marker.setOptions({ icon: chai.MapContainer.getMapIconOptions(task) });
            };
            return TaskMapCtrl;
        })();
        angular.module('omnitechApp', ['ngResource']).controller('TaskMapCtrl', TaskMapCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=TaskMapCtrl.js.map