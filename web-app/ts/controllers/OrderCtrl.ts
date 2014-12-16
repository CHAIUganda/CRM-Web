///<reference path='../_all.ts'/>
module omnitech.chai {

    interface IOrderScope extends HasError,ng.IScope {
        searchCustomerByName : (searchTerm:string) =>ng.IPromise<Customer[]>;
        onSelectCustomer : (customer:Customer) => void;
        lineCost : (li:LineItem) => void;
        orderCost : (order:Order) => number;
        order  : Order
        products : Product[];
        createLineItem : () => void;
        addLineItem: () => void;
        onProductSelected: ()=>void;
        deleteLine: (idx:number) => void;
        editLine : (idx:number)=> void;
        onSave : () => void;
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

            scope.createLineItem = () => scope.order.activeLineItem = this.createLineItem();

            scope.addLineItem = () => {
                $("#line-item-form").modal('hide');
                var lineItems = scope.order.lineItems;

                if (lineItems.indexOf(scope.order.activeLineItem) < 0)
                    lineItems.push(scope.order.activeLineItem);
            };

            scope.onProductSelected = () => this.onProductSelected();

            scope.deleteLine = (idx) => scope.order.lineItems.splice(idx, 1);

            scope.editLine = (idx) => {
                scope.order.activeLineItem = scope.order.lineItems[idx];
            };

            scope.onSave = ()=>this.save();
        }

        private save() {
            Utils.safe(this.scope,
                ()=> {
                    this.dataLoader.persistOrder(this.scope.order)
                        .success(() => {
                            Utils.postError(this.scope, 'Success')
                        })
                        .error((data)=> {
                            Utils.postError(this.scope, data);
                        });
                }, 'Saving order failed')

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


        private static lineCost(li:LineItem):number {
            if (li.unitPrice && li.quantity)
                return li.unitPrice * li.quantity;
            return 0
        }

        private static orderCost(order:Order):number {
            return <number>order.lineItems.reduce((prv, cur)=>(<number>prv) + OrderCtrl.lineCost(cur), 0);
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
