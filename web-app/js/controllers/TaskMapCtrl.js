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
                scope.showCustomers = false;
                this.mapC.showFiltered(function (t) { return t.type == 'task'; });
                scope.momentFromNow = function (date) {
                    if (!date)
                        return "";
                    return moment(date).fromNow();
                };
                scope.persistDueDate = function () {
                    _this.persistDateTask(scope, dataLoader);
                };
                $('#dueDateText').datepicker().on('changeDate', function (ev) {
                    TaskMapCtrl.updateTaskDate(ev.date, scope.task);
                    _this.mapC.renderItem(scope.task);
                    scope.$apply();
                });
                scope.onCreateNewTask = function () {
                    scope.newTask = TaskMapCtrl.createTask(scope.task);
                    $('#newTaskDate').val('');
                };
                scope.onTaskSelected = function (t) { return _this.mapC.centerTask(t); };
                scope.persistNewTask = function () {
                    _this.saveNewTask();
                };
                $('#newTaskDate').datepicker().on('changeDate', function (ev) {
                    TaskMapCtrl.updateTaskDate(ev.date, scope.newTask);
                    scope.$apply();
                });
                scope.onShowCustomers = function () {
                    //invert coz the show customers flag is not yet updated
                    if (!scope.showCustomers) {
                        _this.mapC.showAll();
                    }
                    else {
                        _this.mapC.showFiltered(function (t) { return t.type == 'task'; });
                    }
                };
                scope.onLegendFilter = function (expr) {
                    _this.mapC.showFiltered(function (t) {
                        if (scope.showCustomers)
                            return eval(expr) || (t.type == 'customer');
                        return eval(expr);
                    });
                };
                scope.allTasks = chaiMapData;
            }
            TaskMapCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', TaskMapCtrl];
            };
            TaskMapCtrl.prototype.persistDateTask = function (scope, dataLoader) {
                var date = moment(scope.task.dueDate).format('YYYY-MM-DD');
                dataLoader.persistTaskDate(scope.task, date).success(function () { return chai.Utils.postError(scope, 'Success'); }).error(function (msg) { return chai.Utils.postError(scope, msg); });
            };
            TaskMapCtrl.prototype.saveNewTask = function () {
                var _this = this;
                var task = this.scope.newTask;
                var date = moment(task.dueDate).format('YYYY-MM-DD');
                this.dataLoader.persistNewTask(task, date).error(function (msg) {
                    chai.Utils.postError(_this.scope, msg);
                }).success(function (msg) {
                    _this.mapC.removeElement(_this.scope.task);
                    _this.mapC.addElement(_this.scope.newTask);
                    _this.scope.newTask.id = msg;
                });
            };
            TaskMapCtrl.updateTaskDate = function (date, task) {
                task.dueDate = date;
                task.dueDays = chai.Utils.dayDiff(new Date(), date);
            };
            TaskMapCtrl.createTask = function (c) {
                return {
                    customerId: c.id,
                    customer: c.outletName,
                    customerDescription: c.descriptionOfOutletLocation,
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
        angular.module('omnitechApp', ['ngResource', 'ui.bootstrap']).controller('TaskMapCtrl', TaskMapCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=TaskMapCtrl.js.map