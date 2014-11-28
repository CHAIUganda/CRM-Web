///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var MapContainer = (function () {
            function MapContainer(data) {
                this.data = data;
                this.createMap();
            }
            MapContainer.prototype.createMap = function () {
                var _this = this;
                this.gmap = new GMaps({ lat: 1.354255, lng: 32.314228, div: "#map", zoom: 7 });
                this.data.forEach(function (item) {
                    if (item.lat && item.lng)
                        _this.gmap.addMarker({
                            lat: item.lat,
                            lng: item.lng,
                            title: item.description,
                            icon: "http://labs.google.com/ridefinder/images/mm_20_blue.png",
                            infoWindow: {
                                content: '<p>HTML Content</p>'
                            }
                        });
                });
            };
            return MapContainer;
        })();
        var cont = new MapContainer(chaiMapData);
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
