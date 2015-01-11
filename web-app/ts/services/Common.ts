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

        findWholeSalerSubCounties(wholeSaler:number, district:number):SubCounty[] {
            var url = omnitechBase + '/wholeSaler/findMappedSubCounties';
            return <SubCounty[]>this.resouce(url).query({district: district, territory: wholeSaler})
        }

        getTerritory(id:string):Territory {
            var url = omnitechBase + '/territory/territoryAsJson/' + id;
            return this.resouce(url).get()
        }

        persistSubCountyMap(territory:number, district:number, subCounties:number[]):HttPromise {
            var url = omnitechBase + '/territory/mapTerritoryToSubCounties';
            return <HttPromise>this.http.post(url, {territory: territory, district: district, subCounties: subCounties})
        }

        persistSubCountyMapToWholeSaler(wholerSalerId:number, district:number, subCounties:number[]):HttPromise {
            var url = omnitechBase + '/wholeSaler/mapToSubCounties';
            return <HttPromise>this.http.post(url, {
                wholeSaler: wholerSalerId,
                district: district,
                subCounties: subCounties
            })
        }

        searchForCustomers(searchParam:string):ng.IPromise<Customer[]> {
            var url = omnitechBase + '/rest/customer/searchByName';
            return this.http.get(url, {params: {term: searchParam}}).then((res:ng.IHttpPromiseCallbackArg<Customer[]>) => res.data)
        }

        persistOrder(order:Order):HttPromise {
            var url = omnitechBase + '/call/saveOrUpdate';
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

        persistTaskDate(task:Task,date:string):HttPromise {
            var url = omnitechBase + '/task/updateTaskDate';
            return this.http.post(url, {taskId: task.id, date: date});

        }


    }

    export class Utils {

        static postError(hasError:HasError, error:string) {
            hasError.error = error ? error : 'Technical Error';
            setTimeout(()=> {
                hasError.error = null
            }, 2000)
        }

        static  safe(hasError:HasError, fun:()=>any, message:string) {
            try {
                return fun()
            } catch (Error) {
                var msg = Error.message + ': ' + message;
                Utils.postError(hasError, msg)
            }
        }

        static dayDiff(first:Date, second:Date):number {
            var number2 = (<any>second - <any>first) / (1000 * 60 * 60 * 24);
            return Math.round(number2);
        }
    }
}