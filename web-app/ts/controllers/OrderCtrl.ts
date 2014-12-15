///<reference path='../_all.ts'/>
module omnitech.chai {

    interface IOrderScope extends HasError,ng.IScope {
        searchCustomerByName : (searchTerm:string) =>ng.IPromise<Customer[]>;
        onSelectCustomer : (customer:Customer) => void;
        lineCost : (li:LineItem) => void;
        orderCost : (order:Order) => number;
        order  : Order
        products : Product[];
        addLineItem : () => void;
        onProductSelected: ()=>void;
    }

    class OrderCtrl {

        public static injection() {
            return ['$scope', 'dataLoader', 'filterFilter', OrderCtrl]
        }

        constructor(private scope:IOrderScope, private dataLoader:DataLoader, private filterFilter) {

            this.initData();

            scope.searchCustomerByName = (searchTerm)=> dataLoader.searchForCustomers(searchTerm);

            scope.onSelectCustomer = (c)=> scope.order.customer = c;

            scope.lineCost = (li) => OrderCtrl.lineCost(li);

            scope.orderCost = (o) => OrderCtrl.orderCost(o);

            scope.addLineItem = () => this.addLineItem();

            scope.onProductSelected = () => this.onProductSelected();
        }

        private onProductSelected() {
            var li = this.scope.order.activeLineItem;
            var p = this.getSelectedProduct();
            li.product = p;
            li.unitPrice = p.unitPrice;
        }

        private getSelectedProduct():Product {
            var filter = (p)=> {
                //use == to allow coercion
                return p.id == this.scope.order.activeLineItem.productId;
            };
            var prods = _products.filter(filter);
            return prods[0];
        }

        private addLineItem() {
            var li:LineItem = {};
            this.scope.order.lineItems.push(li);
            this.scope.order.activeLineItem = li;
        }

        private static lineCost(li:LineItem):number {
            if (li.unitPrice && li.quantity)
                return li.unitPrice * li.quantity;
            return 0
        }

        private static orderCost(order:Order):number {
            return <number>order.lineItems.reduce((prv, cur)=>OrderCtrl.lineCost(prv) + OrderCtrl.lineCost(cur), 0);
        }

        private initData() {
            this.scope.order = OrderCtrl.createOrder();
        }

        static createOrder():Order {
            return {activeLineItem: {}, lineItems: []}
        }

        createLineItem():LineItem {
            return {}
        }

    }


    angular.module('omnitechApp', ['ngResource', 'ui.bootstrap'])
        .controller('OrderCtrl', OrderCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())
}
