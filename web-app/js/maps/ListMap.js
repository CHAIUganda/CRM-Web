///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var MapContainer = (function () {
            function MapContainer(data, onClickCallBack) {
                this.data = data;
                this.onClickCallBack = onClickCallBack;
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
                            icon: {
                                path: google.maps.SymbolPath.CIRCLE,
                                scale: 4,
                                fillColor: MapContainer.getColor(item.dueDays),
                                strokeColor: MapContainer.getColor(item.dueDays)
                            },
                            infoWindow: {
                                content: '<p>' + item.description + '</p>'
                            },
                            click: function (marker) {
                                _this.onClickCallBack(item, marker);
                            }
                        });
                });
            };
            MapContainer.getColor = function (days) {
                //http://colorbrewer2.org/
                if (days == 1)
                    return '#e41a1c';
                if (days == 2)
                    return '#377eb8';
                if (days == 3)
                    return '#4daf4a';
                if (days == 4)
                    return '#984ea3';
                if (days >= 30)
                    return '#ff7f00';
                if (days >= 20)
                    return '#ffff33';
                if (days >= 15)
                    return '#a65628';
                if (days >= 10)
                    return '#f781bf';
                if (days >= 5)
                    return '#999999';
            };
            return MapContainer;
        })();
        chai.MapContainer = MapContainer;
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=ListMap.js.map