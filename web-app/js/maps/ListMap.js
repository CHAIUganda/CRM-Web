///<reference path='../_all.ts'/>
var omnitech;
(function (omnitech) {
    var chai;
    (function (chai) {
        var MapContainer = (function () {
            function MapContainer(data, onClickCallBack) {
                this.data = data;
                this.onClickCallBack = onClickCallBack;
                this.latLngBounds = new google.maps.LatLngBounds();
                this.createMap();
            }
            MapContainer.prototype.createMap = function () {
                var _this = this;
                this.gmap = new GMaps({ lat: 1.354255, lng: 32.314228, div: "#map", zoom: 3 });
                this.data.forEach(function (item) {
                    if (item.lat && item.lng) {
                        _this.gmap.addMarker({
                            lat: item.lat,
                            lng: item.lng,
                            icon: MapContainer.getMapIconOptions(item),
                            infoWindow: {
                                content: '<div>' + item.description + '</div>'
                            },
                            click: function (marker) {
                                _this.onClickCallBack(item, marker);
                            }
                        });
                        _this.latLngBounds.extend(new google.maps.LatLng(item.lat, item.lng));
                    }
                });
                this.gmap.map.fitBounds(this.latLngBounds);
            };
            MapContainer.getMapIconOptions = function (item) {
                return {
                    path: google.maps.SymbolPath.CIRCLE,
                    scale: 4,
                    fillColor: MapContainer.getColor(item.dueDays),
                    strokeColor: MapContainer.getColor(item.dueDays)
                };
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
                if (days >= 14)
                    return '#ffff33';
                if (days >= 11)
                    return '#a65628';
                if (days >= 8)
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