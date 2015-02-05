///<reference path='../_all.ts'/>
module omnitech.chai {

    interface ITaskScope extends HasError,ng.IScope {
        task: Task
        newTask: Task
        marker : MarkerWithLabel
        momentFromNow : (date:Date) => string
        persistDueDate : () => void
        onCreateNewTask : () => void
        persistNewTask : () => void
        showCustomers : boolean
    }

    class TaskMapCtrl {

        private mapC:MapContainer;


        static injection():any[] {
            return ['$scope', 'dataLoader', 'filterFilter', TaskMapCtrl]
        }

        constructor(private scope:ITaskScope, private dataLoader:DataLoader, private filterFilter) {


            this.mapC = new MapContainer(chaiMapData, (task, marker)=> {
                scope.task = task;
                scope.marker = marker;
                scope.$apply();
            });

            scope.momentFromNow = (date:Date) => {
                if (!date) return "";
                return moment(date).fromNow()
            };

            scope.persistDueDate = () => {
                this.persistDateTask(scope, dataLoader);
                Utils.postError(scope, 'Success')
            };


            $('#dueDateText').datepicker().on('changeDate', (ev:any)=> {
                TaskMapCtrl.updateTaskDate(ev.date, scope.task);
                this.mapC.renderItem(scope.task);
                scope.$apply();
            });

            scope.onCreateNewTask = () => {
                scope.newTask = TaskMapCtrl.createTask(<Customer><any>scope.task);
            };

            scope.persistNewTask = () => {
                this.saveNewTask();
            };

            $('#newTaskDate').datepicker().on('changeDate', (ev:any)=> {
                TaskMapCtrl.updateTaskDate(ev.date, scope.newTask);
                scope.$apply()
            });

            scope.$watch('showCustomers', ()=> {
                if (scope.showCustomers) {
                    this.mapC.refresh();
                } else {
                    this.mapC.renderFilter((t)=>t.type === 'task')
                }
            });

        }

        private persistDateTask(scope, dataLoader) {
            var date = moment(scope.task.dueDate).format('YYYY-MM-DD');
            dataLoader.persistTaskDate(scope.task, date)
                .success(()=>Utils.postError(scope, 'Success'))
                .error((msg)=>Utils.postError(scope, msg));
        }


        private saveNewTask() {
            this.mapC.removeElement(this.scope.task);
            this.mapC.addElement(this.scope.newTask);
        }

        private static updateTaskDate(date:Date, task:Task):void {
            task.dueDate = date;
            task.dueDays = Utils.dayDiff(new Date(), date);
        }

        private static createTask(c:Customer):Task {
            return {
                customerId: c.id,
                lng: c.lng,
                lat: c.lat,
                title: c.outletName,
                description: c.outletName,
                dueDate: new Date(),
                type: 'task',
                segment: c.segment
            }
        }
    }

    angular.module('omnitechApp', ['ngResource'])
        .controller('TaskMapCtrl', TaskMapCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())

}