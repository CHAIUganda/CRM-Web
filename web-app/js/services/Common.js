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
            return DataLoader;
        })();
        chai.DataLoader = DataLoader;
        var Utils = (function () {
            function Utils() {
            }
            Utils.postError = function (hasError, error) {
                hasError.error = error;
                setTimeout(function () {
                    hasError.error = null;
                }, 2000);
            };
            return Utils;
        })();
        chai.Utils = Utils;
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=Common.js.map