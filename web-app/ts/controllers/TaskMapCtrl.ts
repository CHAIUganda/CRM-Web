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
        onShowCustomers : () => void
        onLegendFilter : (expr:string) => void
        onTaskSelected : (t:Task) => void
        allTasks: Task[]
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
            scope.showCustomers = false;
            this.mapC.showFiltered((t)=>t.type == 'task');

            scope.momentFromNow = (date:Date) => {
                if (!date) return "";
                return moment(date).fromNow()
            };

            scope.persistDueDate = () => {
                this.persistDateTask(scope, dataLoader);
            };


            $('#dueDateText').datepicker().on('changeDate', (ev:any)=> {
                TaskMapCtrl.updateTaskDate(ev.date, scope.task);
                this.mapC.renderItem(scope.task);
                scope.$apply();
            });

            scope.onCreateNewTask = () => {
                scope.newTask = TaskMapCtrl.createTask(<Customer><any>scope.task);
                $('#newTaskDate').val('');
            };

            scope.onTaskSelected = (t)=>this.mapC.centerTask(t);


            scope.persistNewTask = () => {
                this.saveNewTask();
            };

            $('#newTaskDate').datepicker().on('changeDate', (ev:any)=> {
                TaskMapCtrl.updateTaskDate(ev.date, scope.newTask);
                scope.$apply();

            });

            scope.onShowCustomers = () => {
                //invert coz the show customers flag is not yet updated
                if (!scope.showCustomers) {
                    this.mapC.showAll();
                } else {
                    this.mapC.showFiltered((t)=>t.type == 'task')
                }
            };

            scope.onLegendFilter = (expr) => {
                this.mapC.showFiltered((t) => {
                    if (scope.showCustomers)
                        return (<boolean>eval(expr)) || (t.type == 'customer');
                    return <boolean>eval(expr)
                });
            };

            scope.allTasks = chaiMapData

        }

        private persistDateTask(scope, dataLoader) {
            var date = moment(scope.task.dueDate).format('YYYY-MM-DD');
            dataLoader.persistTaskDate(scope.task, date)
                .success(()=>Utils.postError(scope, 'Success'))
                .error((msg)=>Utils.postError(scope, msg));
        }


        private saveNewTask() {
            var task = this.scope.newTask;
            var date = moment(task.dueDate).format('YYYY-MM-DD')
            this.dataLoader.persistNewTask(task, date)
                .error((msg)=> {
                    Utils.postError(this.scope, msg);
                }).success((msg:string) => {
                    this.mapC.removeElement(this.scope.task);
                    this.mapC.addElement(this.scope.newTask);
                    this.scope.newTask.id = msg;
                });
        }

        private static updateTaskDate(date:Date, task:Task):void {
            task.dueDate = date;
            task.dueDays = Utils.dayDiff(new Date(), date);
        }

        private static createTask(c:Customer):Task {
            return {
                customerId: c.id,
                customer: c.outletName,
                customerDescription: c.descriptionOfOutletLocation,
                lng: c.lng,
                lat: c.lat,
                title: c.outletName,
                description: 'Detailing ['+c.outletName+']',
                dueDate: new Date(),
                type: 'task',
                segment: c.segment
            }
        }
    }

    angular.module('omnitechApp', ['ngResource', 'ui.bootstrap'])
        .controller('TaskMapCtrl', TaskMapCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())

}