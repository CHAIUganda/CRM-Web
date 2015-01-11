///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var CallCtrl = (function () {
            function CallCtrl(scope, dataLoader, filterFilter) {
                this.scope = scope;
                this.dataLoader = dataLoader;
                this.filterFilter = filterFilter;
                scope.searchCustomerByName = function (searchTerm) { return dataLoader.searchForCustomers(searchTerm); };
                scope.onSelectCustomer = function (c) {
                    console.log(c);
                    scope.customer = c;
                };
            }
            CallCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', CallCtrl];
            };
            return CallCtrl;
        })();
        angular.module('omnitechApp', ['ngResource', 'ui.bootstrap']).controller('CallCtrl', CallCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=CallCtrl.js.map