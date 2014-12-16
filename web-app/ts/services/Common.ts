///<reference path='../_all.ts'/>
module omnitech.chai {
    'use strict';

    export class DataLoader {

        public injection():any[] {
            return ['$http', '$resource', DataLoader]
        }

        constructor(private http:ng.IHttpService, private resouce:any) {
        }


        findMappedSubCounties(territory:number, district:number):SubCounty[] {
            var url = omnitechBase + '/territory/findMappedSubCounties';
            return <SubCounty[]>this.resouce(url).query({district: district, territory: territory})
        }

        getTerritory(id:string):Territory {
            var url = omnitechBase + '/territory/territoryAsJson/' + id;
            return this.resouce(url).get()
        }

        persistSubCountyMap(territory:number, district:number, subCounties:number[]):HttPromise {
            var url = omnitechBase + '/territory/mapTerritoryToSubCounties';
            return <HttPromise>this.http.post(url, {territory: territory, district: district, subCounties: subCounties})
        }

        searchForCustomers(searchParam:string):ng.IPromise<Customer[]> {
            var url = omnitechBase + '/rest/customer/searchByName';
            return this.http.get(url, {params: {term: searchParam}}).then((res:ng.IHttpPromiseCallbackArg<Customer[]>) => res.data)
        }

        persistOrder(order:Order):HttPromise {
            var url = omnitechBase + '/order/saveOrUpdate';
            var jsonFriendlyOrder = {
                id: order.id,
                customerId: order.customer.id,
                description: order.comment,
                lineItems: order.lineItems.map((m) => {
                    return {'productId': m.productId, quantity: m.quantity}
                })
            };
            return this.http.post(url, JSON.stringify(jsonFriendlyOrder));
        }


    }

    export class Utils {

        static postError(hasError:HasError, error:string) {
            hasError.error = error;
            setTimeout(()=> {
                hasError.error = null
            }, 2000)
        }
    }
}