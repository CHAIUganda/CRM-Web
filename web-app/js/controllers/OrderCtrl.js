///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var OrderCtrl = (function () {
            function OrderCtrl(scope, dataLoader, filterFilter) {
                this.scope = scope;
                this.dataLoader = dataLoader;
                this.filterFilter = filterFilter;
                this.initData();
                scope.searchCustomerByName = function (searchTerm) { return dataLoader.searchForCustomers(searchTerm); };
                scope.onSelectCustomer = function (c) { return scope.order.customer = c; };
            }
            OrderCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', OrderCtrl];
            };
            OrderCtrl.prototype.initData = function () {
                this.scope.products = [{ name: 'SKks' }, { name: 'Sijjd' }];
                this.scope.order = {};
            };
            return OrderCtrl;
        })();
        angular.module('omnitechApp', ['ngResource', 'ui.bootstrap']).controller('OrderCtrl', OrderCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=OrderCtrl.js.map