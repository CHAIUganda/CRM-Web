///<reference path='../_all.ts'/>
module omnitech.chai {


    interface ITerritoryScope extends ng.IScope {
        onRemap : (id:string)=>void;
        territory : Territory;
        districtId :number;
        onDistrictSelected: string;
        subCounties : SubCounty[];
    }

    class TerritoryMapCtrl {

        public injection():any[] {
            return ['$scope', 'dataLoader', 'filterFilter', TerritoryMapCtrl]
        }

        constructor(private scope:ITerritoryScope, private dataLoader:DataLoader, private filterFilter) {

            scope.onRemap = (id)=> {
                scope.territory = dataLoader.getTerritory(id)
            };

            scope.$watch('districtId', ()=>this.onDistrictChanged());

        }

        private onDistrictChanged() {
            if (!this.scope.territory) {
                return;
            }
            this.scope.subCounties = this.dataLoader.findMappedSubCounties(this.scope.territory.id, this.scope.districtId)
        }

    }

    var territoryApp = angular.module('omnitechApp', ['ngResource'])
        .controller('TerritoryMapCtrl', TerritoryMapCtrl.prototype.injection())
        .service('dataLoader', DataLoader.prototype.injection())


}
