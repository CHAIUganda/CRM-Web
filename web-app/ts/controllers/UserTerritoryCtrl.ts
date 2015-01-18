///<reference path='../_all.ts'/>
module omnitech.chai {


    interface IUserTerritoryScope extends HasError,ng.IScope {
        onRemap : (id:string)=>void;
        territory : Territory;
        user : User;
        districtId :number;
        onDistrictSelected: string;
        childItems : Territory[];
        onSave: () => void;
        onToggleAll : () => void;
    }

    class UserTerritoryCtrl {

        public static injection():any[] {
            return ['$scope', 'dataLoader', 'filterFilter', UserTerritoryCtrl]
        }

        constructor(private scope:IUserTerritoryScope, private dataLoader:DataLoader, private filterFilter) {

            scope.onRemap = (id)=> {
                scope.user = dataLoader.getUser(id);
            };

            scope.onSave = ()=> {
                this.onSave();
            };

            scope.onToggleAll = ()=> {
                this.checkAll();
            };

            scope.$watch('territoryId', ()=>this.onTerritoryChanged());
            scope.$watch('territory.id', ()=>this.onTerritoryChanged());
        }

        private checkAll() {
            if (!this.scope.childItems) {
                return;
            }
            if (this.scope.childItems.some((obj)=>obj.mapped)) {
                this.scope.childItems.forEach((obj:SubCounty)=>obj.mapped = false)
            } else {
                this.scope.childItems.forEach((obj:SubCounty)=>obj.mapped = true)
            }
        }

        private onTerritoryChanged() {
            if (!this.scope.territory) {
                return;
            }
            this.scope.childItems = this.dataLoader.findWholeSalerSubCounties(this.scope.territory.id, this.scope.districtId)
        }

        private onSave() {
            if (!this.scope.childItems) {
                Utils.postError(this.scope, 'Please first select A District');
                return
            }
            var subIds = this.scope.childItems.filter((obj)=>obj.mapped).map((obj)=>obj.id);
            this.dataLoader.persistSubCountyMapToWholeSaler(this.scope.territory.id, this.scope.districtId, subIds)
                .success(()=> {
                    this.onTerritoryChanged();
                    Utils.postError(this.scope, 'Success');
                }).error((data)=> {
                    Utils.postError(this.scope, 'Error: ' + data);
                });
        }


    }

    angular.module('omnitechApp', ['ngResource'])
        .controller('UserTerritoryCtrl', UserTerritoryCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())


}
