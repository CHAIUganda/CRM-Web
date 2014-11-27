///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    (function (chai) {
        var MapContainer = (function () {
            function MapContainer(data) {
                this.data = data;
                this.createMap();
            }
            MapContainer.prototype.createMap = function () {
                this.gmap = new GMaps({ lat: 0.639978, lng: 30.2308269, div: "#map", zoom: 7 });
            };
            return MapContainer;
        })();

        var cont = new MapContainer();
    })(omnitech.chai || (omnitech.chai = {}));
    var chai = omnitech.chai;
})(omnitech || (omnitech = {}));
//# sourceMappingURL=ListMap.js.map
