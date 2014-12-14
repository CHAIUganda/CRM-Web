///<reference path='../_all.ts'/>
module omnitech.chai {

    interface IOrderScope extends HasError,ng.IScope {
        searchCustomerByName : (searchTerm:string) =>ng.IPromise<Customer[]>;
        onSelectCustomer : (customer:Customer) => void;
        order  : Order


    }

    class OrderCtrl {

        public static injection() {
            return ['$scope', 'dataLoader', 'filterFilter', OrderCtrl]
        }

        constructor(private scope:IOrderScope, private dataLoader:DataLoader, private filterFilter) {

            this.initData();

            scope.searchCustomerByName = (searchTerm)=> dataLoader.searchForCustomers(searchTerm);


            scope.onSelectCustomer = (c)=> scope.order.customer = c;


        }

        private initData() {
                this.scope.order = {customer : null}
        }


    }

    angular.module('omnitechApp', ['ngResource', 'ui.bootstrap'])
        .controller('OrderCtrl', OrderCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())
}
