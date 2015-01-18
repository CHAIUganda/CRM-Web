///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var CallCtrl = (function () {
            function CallCtrl(scope, dataLoader) {
                this.scope = scope;
                this.dataLoader = dataLoader;
                scope.searchCustomerByName = function (searchTerm) { return dataLoader.searchForCustomers(searchTerm); };
                scope.onSelectCustomer = function (c) { return scope.customer = c; };
                if (typeof _orderCustomer !== undefined) {
                    scope.customer = _orderCustomer;
                }
            }
            CallCtrl.injection = function () {
                return ['$scope', 'dataLoader', CallCtrl];
            };
            return CallCtrl;
        })();
        angular.module('omnitechApp', ['ngResource', 'ui.bootstrap']).controller('CallCtrl', CallCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=CallCtrl.js.map