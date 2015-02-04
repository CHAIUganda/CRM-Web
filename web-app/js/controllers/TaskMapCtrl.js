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
                    //marker.setMap(null);
                    scope.$apply();
                });
                scope.momentFromNow = function (date) {
                    if (!date)
                        return "";
                    return moment(date).fromNow();
                };
                scope.persistDueDate = function () {
                    _this.persistDateTask(scope, dataLoader);
                    chai.Utils.postError(scope, 'Success');
                };
                $('#dueDateText').datepicker().on('changeDate', function (ev) {
                    TaskMapCtrl.updateTaskDate(ev.date, scope.task);
                    _this.scope.marker.setOptions({ icon: chai.MapContainer.getMapIconOptions(scope.task) });
                    scope.$apply();
                });
                scope.onCreateNewTask = function () {
                    scope.newTask = TaskMapCtrl.createTask(scope.task);
                };
                scope.persistNewTask = function () {
                    _this.saveNewTask();
                };
                $('#newTaskDate').datepicker().on('changeDate', function (ev) {
                    TaskMapCtrl.updateTaskDate(ev.date, scope.newTask);
                    scope.$apply();
                });
            }
            TaskMapCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', TaskMapCtrl];
            };
            TaskMapCtrl.prototype.persistDateTask = function (scope, dataLoader) {
                var date = moment(scope.task.dueDate).format('YYYY-MM-DD');
                dataLoader.persistTaskDate(scope.task, date).success(function () { return chai.Utils.postError(scope, 'Success'); }).error(function (msg) { return chai.Utils.postError(scope, msg); });
            };
            TaskMapCtrl.prototype.saveNewTask = function () {
                this.mapC.removeElement(this.scope.task);
                this.mapC.addElement(this.scope.newTask);
            };
            TaskMapCtrl.updateTaskDate = function (date, task) {
                task.dueDate = date;
                task.dueDays = chai.Utils.dayDiff(new Date(), date);
            };
            TaskMapCtrl.createTask = function (c) {
                return {
                    customerId: c.id,
                    lng: c.lng,
                    lat: c.lat,
                    title: c.outletName,
                    description: c.outletName,
                    dueDate: new Date(),
                    type: 'task',
                    segment: c.segment
                };
            };
            return TaskMapCtrl;
        })();
        angular.module('omnitechApp', ['ngResource']).controller('TaskMapCtrl', TaskMapCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=TaskMapCtrl.js.map