///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var OrderCtrl = (function () {
            function OrderCtrl(scope, dataLoader, filterFilter) {
                var _this = this;
                this.scope = scope;
                this.dataLoader = dataLoader;
                this.filterFilter = filterFilter;
                this.initData();
                scope.searchCustomerByName = function (searchTerm) { return dataLoader.searchForCustomers(searchTerm); };
                scope.onSelectCustomer = function (c) { return scope.order.customer = c; };
                scope.lineCost = function (li) { return OrderCtrl.lineCost(li); };
                scope.orderCost = function (o) { return OrderCtrl.orderCost(o); };
                scope.createLineItem = function () { return scope.order.activeLineItem = _this.createLineItem(); };
                scope.addLineItem = function () {
                    $("#line-item-form").modal('hide');
                    var lineItems = scope.order.lineItems;
                    if (lineItems.indexOf(scope.order.activeLineItem) < 0)
                        lineItems.push(scope.order.activeLineItem);
                };
                scope.onProductSelected = function () { return _this.onProductSelected(); };
                scope.deleteLine = function (idx) { return scope.order.lineItems.splice(idx, 1); };
                scope.editLine = function (idx) {
                    scope.order.activeLineItem = scope.order.lineItems[idx];
                };
                scope.onSave = function () { return _this.save(); };
            }
            OrderCtrl.injection = function () {
                return ['$scope', 'dataLoader', 'filterFilter', OrderCtrl];
            };
            OrderCtrl.prototype.save = function () {
                var _this = this;
                chai.Utils.safe(this.scope, function () {
                    _this.dataLoader.persistOrder(_this.scope.order).success(function () {
                        chai.Utils.postError(_this.scope, 'Success');
                    }).error(function (data) {
                        chai.Utils.postError(_this.scope, data);
                    });
                }, 'Saving order failed');
            };
            OrderCtrl.prototype.onProductSelected = function () {
                var li = this.scope.order.activeLineItem;
                var p = this.getSelectedProduct();
                li.product = p;
                li.unitPrice = p.unitPrice;
            };
            OrderCtrl.prototype.getSelectedProduct = function () {
                var _this = this;
                var filter = function (p) {
                    //use == to allow coercion
                    return p.id == _this.scope.order.activeLineItem.productId;
                };
                var prods = _products.filter(filter);
                return prods[0];
            };
            OrderCtrl.lineCost = function (li) {
                if (li.unitPrice && li.quantity)
                    return li.unitPrice * li.quantity;
                return 0;
            };
            OrderCtrl.orderCost = function (order) {
                return order.lineItems.reduce(function (prv, cur) { return prv + OrderCtrl.lineCost(cur); }, 0);
            };
            OrderCtrl.prototype.initData = function () {
                this.scope.order = OrderCtrl.createOrder();
            };
            OrderCtrl.createOrder = function () {
                return { activeLineItem: {}, lineItems: [] };
            };
            OrderCtrl.prototype.createLineItem = function () {
                return {};
            };
            return OrderCtrl;
        })();
        angular.module('omnitechApp', ['ngResource', 'ui.bootstrap']).controller('OrderCtrl', OrderCtrl.injection()).service('dataLoader', chai.DataLoader.prototype.injection());
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=OrderCtrl.js.map