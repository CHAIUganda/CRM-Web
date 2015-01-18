///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var UserTerritoryCtrl = (function () {
            function UserTerritoryCtrl(scope, dataLoader, filterFilter) {
                var _this = this;
                this.scope = scope;
                this.dataLoader = dataLoader;
                this.filterFilter = filterFilter;
                scope.onRemap = function (id) {
                    scope.user = dataLoader.getUser(id);
                };
                scope.onSave = function () {
                    _this.onSave();
                };
                scope.onToggleAll = function () {
                    _this.checkAll();
                };
                scope.$watch('territoryId', function () { return _this.onTerritoryChanged(); });
                scope.$watch('territory.id', function () { return _this.onTerritoryChanged(); });
            }
            UserTerritoryCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', UserTerritoryCtrl];
            };
            UserTerritoryCtrl.prototype.checkAll = function () {
                if (!this.scope.childItems) {
                    return;
                }
                if (this.scope.childItems.some(function (obj) { return obj.mapped; })) {
                    this.scope.childItems.forEach(function (obj) { return obj.mapped = false; });
                }
                else {
                    this.scope.childItems.forEach(function (obj) { return obj.mapped = true; });
                }
            };
            UserTerritoryCtrl.prototype.onTerritoryChanged = function () {
                if (!this.scope.territory) {
                    return;
                }
                this.scope.childItems = this.dataLoader.findWholeSalerSubCounties(this.scope.territory.id, this.scope.districtId);
            };
            UserTerritoryCtrl.prototype.onSave = function () {
                var _this = this;
                if (!this.scope.childItems) {
                    chai.Utils.postError(this.scope, 'Please first select A District');
                    return;
                }
                var subIds = this.scope.childItems.filter(function (obj) { return obj.mapped; }).map(function (obj) { return obj.id; });
                this.dataLoader.persistSubCountyMapToWholeSaler(this.scope.territory.id, this.scope.districtId, subIds).success(function () {
                    _this.onTerritoryChanged();
                    chai.Utils.postError(_this.scope, 'Success');
                }).error(function (data) {
                    chai.Utils.postError(_this.scope, 'Error: ' + data);
                });
            };
            return UserTerritoryCtrl;
        })();
        angular.module('omnitechApp', ['ngResource']).controller('UserTerritoryCtrl', UserTerritoryCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=UserTerritoryCtrl.js.map