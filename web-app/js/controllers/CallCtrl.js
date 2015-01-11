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
            }
            CallCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', CallCtrl];
            };
            CallCtrl.prototype.save = function () {
            };
            CallCtrl.lineCost = function (li) {
                if (li.unitPrice && li.quantity)
                    return li.unitPrice * li.quantity;
                return 0;
            };
            CallCtrl.createOrder = function () {
                return { activeLineItem: {}, lineItems: [] };
            };
            CallCtrl.prototype.createLineItem = function () {
                return {};
            };
            return CallCtrl;
        })();
        angular.module('omnitechApp', ['ngResource', 'ui.bootstrap']).controller('OrderCtrl', CallCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=CallCtrl.js.map