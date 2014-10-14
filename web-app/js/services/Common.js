var omnitech;
(function (omnitech) {
    ///<reference path='../_all.ts'/>
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
            return DataLoader;
        })();
        chai.DataLoader = DataLoader;
    })(omnitech.chai || (omnitech.chai = {}));
    var chai = omnitech.chai;
})(omnitech || (omnitech = {}));
//# sourceMappingURL=Common.js.map
