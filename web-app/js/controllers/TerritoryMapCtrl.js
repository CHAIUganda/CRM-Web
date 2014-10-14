var omnitech;
(function (omnitech) {
    ///<reference path='../_all.ts'/>
    (function (chai) {
        var TerritoryMapCtrl = (function () {
            function TerritoryMapCtrl(scope, dataLoader, filterFilter) {
                var _this = this;
                this.scope = scope;
                this.dataLoader = dataLoader;
                this.filterFilter = filterFilter;
                scope.onRemap = function (id) {
                    scope.territory = dataLoader.getTerritory(id);
                };

                scope.$watch('districtId', function () {
                    return _this.onDistrictChanged();
                });
            }
            TerritoryMapCtrl.prototype.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', TerritoryMapCtrl];
            };

            TerritoryMapCtrl.prototype.onDistrictChanged = function () {
                if (!this.scope.territory) {
                    return;
                }
                this.scope.subCounties = this.dataLoader.findMappedSubCounties(this.scope.territory.id, this.scope.districtId);
            };
            return TerritoryMapCtrl;
        })();

        var territoryApp = angular.module('omnitechApp', ['ngResource']).controller('TerritoryMapCtrl', TerritoryMapCtrl.prototype.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(omnitech.chai || (omnitech.chai = {}));
    var chai = omnitech.chai;
})(omnitech || (omnitech = {}));
//# sourceMappingURL=TerritoryMapCtrl.js.map
