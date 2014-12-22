///<reference path='../_all.ts'/>
module omnitech.chai {

    interface ITaskScope extends HasError,ng.IScope {
        task: Task
    }

    class TaskMapCtrl {

        private mapC:MapContainer;


        static injection():any[] {
            return ['$scope', 'dataLoader', 'filterFilter', TaskMapCtrl]
        }

        constructor(private scope:ITaskScope, private dataLoader:DataLoader, private filterFilter) {


            this.mapC = new MapContainer(chaiMapData, (task, marker)=> {
                scope.task = task;
                scope.$apply();
            });



        }


    }

    angular.module('omnitechApp', ['ngResource'])
        .controller('TaskMapCtrl', TaskMapCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())

}