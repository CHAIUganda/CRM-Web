///<reference path='../_all.ts'/>
module omnitech.chai {

    interface ICallScope extends HasError,ng.IScope {
        searchCustomerByName : (searchTerm:string) =>ng.IPromise<Customer[]>;
        onSelectCustomer : (customer:Customer) => void;
        customer: Customer;
    }

    class CallCtrl {

        public static injection() {
            return ['$scope', 'dataLoader', CallCtrl]
        }

        constructor(private scope:ICallScope, private dataLoader:DataLoader) {

            scope.searchCustomerByName = (searchTerm)=> dataLoader.searchForCustomers(searchTerm);
            scope.onSelectCustomer = (c)=> scope.customer = c;

            if (typeof _orderCustomer !== undefined) {
                scope.customer = _orderCustomer;
            }
        }
    }


    angular.module('omnitechApp', ['ngResource', 'ui.bootstrap'])
        .controller('CallCtrl', CallCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())
}
