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

                scope.onSave = function () {
                    _this.onSave();
                };

                scope.$watch('districtId', function () {
                    return _this.onDistrictChanged();
                });
                scope.$watch('territory.id', function () {
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

            TerritoryMapCtrl.prototype.onSave = function () {
                var _this = this;
                if (!this.scope.subCounties) {
                    this.scope.error = 'Please first select A District';
                    return;
                }
                var subIds = this.scope.subCounties.filter(function (obj) {
                    return obj.mapped;
                }).map(function (obj) {
                    return obj.id;
                });
                this.dataLoader.persistSubCountyMap(this.scope.territory.id, this.scope.districtId, subIds).success(function () {
                    _this.onDistrictChanged();
                    _this.scope.error = 'Success';
                }).error(function (data) {
                    _this.scope.error = 'Error: ' + data;
                });
            };
            return TerritoryMapCtrl;
        })();

        angular.module('omnitechApp', ['ngResource']).controller('TerritoryMapCtrl', TerritoryMapCtrl.prototype.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(omnitech.chai || (omnitech.chai = {}));
    var chai = omnitech.chai;
})(omnitech || (omnitech = {}));
//# sourceMappingURL=TerritoryMapCtrl.js.map
