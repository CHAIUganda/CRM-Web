///<reference path='../_all.ts'/>
module omnitech.chai {

    interface ICallScope extends HasError,ng.IScope {
        searchCustomerByName : (searchTerm:string) =>ng.IPromise<Customer[]>;
        onSelectCustomer : (customer:Customer) => void;
    }

    class CallCtrl {

        public static injection() {
            return ['$scope', 'dataLoader', 'filterFilter', CallCtrl]
        }

        constructor(private scope:ICallScope, private dataLoader:DataLoader, private filterFilter) {

            scope.searchCustomerByName = (searchTerm)=> dataLoader.searchForCustomers(searchTerm);


        }

        private save() {

        }


        private static lineCost(li:LineItem):number {
            if (li.unitPrice && li.quantity)
                return li.unitPrice * li.quantity;
            return 0
        }


        static createOrder():Order {
            return {activeLineItem: {}, lineItems: []}
        }

        createLineItem():LineItem {
            return {}
        }

    }


    angular.module('omnitechApp', ['ngResource', 'ui.bootstrap'])
        .controller('OrderCtrl', CallCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())
}
