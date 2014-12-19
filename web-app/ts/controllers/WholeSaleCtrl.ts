///<reference path='../_all.ts'/>
module omnitech.chai {


    interface ITerritoryScope extends HasError,ng.IScope {
        onRemap : (id:string)=>void;
        territory : Territory;
        districtId :number;
        onDistrictSelected: string;
        subCounties : SubCounty[];
        onSave: () => void;
        onToggleAll : () => void;
    }

    class TerritoryMapCtrl {

        public injection():any[] {
            return ['$scope', 'dataLoader', 'filterFilter', TerritoryMapCtrl]
        }

        constructor(private scope:ITerritoryScope, private dataLoader:DataLoader, private filterFilter) {

            scope.onRemap = (id)=> {
                scope.territory = dataLoader.getTerritory(id);
            };

            scope.onSave = ()=> {
                this.onSave();
            };

            scope.onToggleAll = ()=> {
                this.checkAll();
            };

            scope.$watch('districtId', ()=>this.onDistrictChanged());
            scope.$watch('territory.id', ()=>this.onDistrictChanged());
        }

        private checkAll() {
            if (!this.scope.subCounties) {
                return;
            }
            if (this.scope.subCounties.some((obj)=>obj.mapped)) {
                this.scope.subCounties.forEach((obj:SubCounty)=>obj.mapped = false)
            } else {
                this.scope.subCounties.forEach((obj:SubCounty)=>obj.mapped = true)
            }
        }

        private onDistrictChanged() {
            if (!this.scope.territory) {
                return;
            }
            this.scope.subCounties = this.dataLoader.findWholeSalerSubCounties(this.scope.territory.id, this.scope.districtId)
        }

        private onSave() {
            if (!this.scope.subCounties) {
                Utils.postError(this.scope, 'Please first select A District');
                return
            }
            var subIds = this.scope.subCounties.filter((obj)=>obj.mapped).map((obj)=>obj.id);
            this.dataLoader.persistSubCountyMap(this.scope.territory.id, this.scope.districtId, subIds)
                .success(()=> {
                    this.onDistrictChanged();
                    Utils.postError(this.scope, 'Success');
                }).error((data)=> {
                    Utils.postError(this.scope, 'Error: ' + data);
                });
        }


    }

    angular.module('omnitechApp', ['ngResource'])
        .controller('WholeSaleCtrl', TerritoryMapCtrl.prototype.injection())
        .service('dataLoader', DataLoader.prototype.injection())


}
