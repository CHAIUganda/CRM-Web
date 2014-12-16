///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        'use strict';
        var DataLoader = (function () {
            function DataLoader(http, resouce) {
                this.http = http;
                this.resouce = resouce;
            }
            DataLoader.prototype.injection = function () {
                return ['$http', '$resource', DataLoader];
            };
            DataLoader.prototype.findMappedSubCounties = function (territory, district) {
                var url = omnitechBase + '/territory/findMappedSubCounties';
                return this.resouce(url).query({ district: district, territory: territory });
            };
            DataLoader.prototype.getTerritory = function (id) {
                var url = omnitechBase + '/territory/territoryAsJson/' + id;
                return this.resouce(url).get();
            };
            DataLoader.prototype.persistSubCountyMap = function (territory, district, subCounties) {
                var url = omnitechBase + '/territory/mapTerritoryToSubCounties';
                return this.http.post(url, { territory: territory, district: district, subCounties: subCounties });
            };
            DataLoader.prototype.searchForCustomers = function (searchParam) {
                var url = omnitechBase + '/rest/customer/searchByName';
                return this.http.get(url, { params: { term: searchParam } }).then(function (res) { return res.data; });
            };
            DataLoader.prototype.persistOrder = function (order) {
                var url = omnitechBase + '/order/saveOrUpdate';
                var jsonFriendlyOrder = {
                    id: order.id,
                    customerId: order.customer.id,
                    description: order.comment,
                    lineItems: order.lineItems.map(function (m) {
                        return { 'productId': m.productId, quantity: m.quantity };
                    })
                };
                return this.http.post(url, JSON.stringify(jsonFriendlyOrder));
            };
            return DataLoader;
        })();
        chai.DataLoader = DataLoader;
        var Utils = (function () {
            function Utils() {
            }
            Utils.postError = function (hasError, error) {
                hasError.error = error ? error : 'Technical Error';
                setTimeout(function () {
                    hasError.error = null;
                }, 2000);
            };
            Utils.safe = function (hasError, fun, message) {
                try {
                    return fun();
                }
                catch (Error) {
                    var msg = Error.message + ': ' + message;
                    Utils.postError(hasError, msg);
                }
            };
            return Utils;
        })();
        chai.Utils = Utils;
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=Common.js.map