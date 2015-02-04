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
                this.gmap = new GMaps({ lat: 1.354255, lng: 32.314228, div: "#map", zoom: 7 });
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
                setTimeout(function () {
                    _this.gmap.map.fitBounds(_this.latLngBounds);
                }, 3000);
                this.gmap.map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(document.getElementById('legend'));
            };
            MapContainer.getMapIconOptions = function (item) {
                if (item.type == 'task') {
                    var segment = item.segment != null ? item.segment : 'Z';
                    if (item.status === 'complete') {
                        var iconFillColor = '4daf4a';
                        return 'http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|' + segment + '|' + iconFillColor + '|FFFFFF|ffff33';
                    }
                    //new tasks
                    var iconFillColor = MapContainer.getColor(item.dueDays).replace('#', '');
                    return 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=' + segment + '|' + iconFillColor + '|FFFFFF';
                }
                else {
                    var segment = item.segment != null ? item.segment : 'Z';
                    var iconFillColor = '4B0082';
                    return 'http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|' + segment + '|' + iconFillColor + '|FFFFFF|ff7f00';
                }
            };
            MapContainer.getColor = function (days) {
                //http://colorbrewer2.org/
                if (days < -1)
                    return '#e41a1c';
                if (days === -1)
                    return '#377eb8';
                if (days === 0)
                    return '#4daf4a';
                if (days === 1)
                    return '#984ea3';
                if (days === 2)
                    return '#ff7f00';
                if (days === 3)
                    return '#ffff33';
                if (days === 4)
                    return '#a65628';
                if (days === 5)
                    return '#f781bf';
                if (days === 6)
                    return '#999999';
                if (days >= 7)
                    return '#000000';
            };
            return MapContainer;
        })();
        chai.MapContainer = MapContainer;
    })(chai = omnitech.chai || (omnitech.chai = {}));
})(omnitech || (omnitech = {}));
//# sourceMappingURL=ListMap.js.map