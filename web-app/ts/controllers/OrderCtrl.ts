///<reference path='../_all.ts'/>
module omnitech.chai {
    interface IOrderScope extends HasError,ng.IScope {

        searchCustomerByName : (searchTerm:string) =>ng.IPromise<Customer[]>;
        onSelectCustomer : (customer:Customer) => void;

    }

    class OrderCtrl {

        public static injection() {
            return ['$scope', 'dataLoader', 'filterFilter', OrderCtrl]
        }

        constructor(private scope:IOrderScope, private dataLoader:DataLoader, private filterFilter) {


            scope.searchCustomerByName = (searchTerm)=> {
                var searchForCustomers = dataLoader.searchForCustomers(searchTerm);
                console.log(searchForCustomers);
                return searchForCustomers;
            };

            scope.onSelectCustomer = (c) => {
                console.log(c)
            };
        }


    }

    angular.module('omnitechApp', ['ngResource', 'ui.bootstrap'])
        .controller('OrderCtrl', OrderCtrl.injection())
        .service('dataLoader', DataLoader.prototype.injection())
}
