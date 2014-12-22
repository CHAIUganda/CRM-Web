///<reference path='../_all.ts'/>
module omnitech.chai {

    interface ITaskScope extends HasError,ng.IScope {
        task: Task
        marker : MarkerWithLabel
        momentFromNow : () => string
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

            scope.momentFromNow = () => {

                if (!scope.task || !scope.task.dueDate) return "";

                //2014-12-31T08:19:17Z
                //'MMMM Do YYYY, h:mm:ss a
                return moment(scope.task.dueDate).fromNow()
            };


            $('#dueDateText').datepicker().on('changeDate', (ev:any)=> {
                this.updateTaskDate(ev.date);
                scope.$apply()
            });


        }

        private updateTaskDate(date:Date):void {
            var task = this.scope.task;
            task.dueDate = date;
            task.dueDays = Utils.dayDiff(new Date(), date);
            this.scope.marker.setOptions({icon: MapContainer.getMapIconOptions(task)});
        }


    }

    angular.module('omnitechApp', ['ngResource'])
        .controller('TaskMapCtrl', TaskMapCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())

}