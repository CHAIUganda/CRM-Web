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


    }
}