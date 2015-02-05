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
                this.refresh();
                setTimeout(function () {
                    _this.gmap.map.fitBounds(_this.latLngBounds);
                }, 3000);
                this.gmap.map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(document.getElementById('legend'));
                this.gmap.map.controls[google.maps.ControlPosition.TOP_CENTER].push(document.getElementById('showCustomers'));
            };
            MapContainer.prototype.renderItem = function (item) {
                if (item.lat && item.lng) {
                    //MapContainer.clearTaskMarker(item);
                    if (item.marker) {
                        item.marker.setOptions(this.getMarkerOptions(item));
                    }
                    else {
                        item.marker = this.gmap.addMarker(this.getMarkerOptions(item));
                    }
                    this.latLngBounds.extend(new google.maps.LatLng(item.lat, item.lng));
                }
            };
            MapContainer.prototype.getMarkerOptions = function (item) {
                var _this = this;
                return {
                    lat: item.lat,
                    lng: item.lng,
                    icon: MapContainer.getIcon(item),
                    infoWindow: {
                        content: '<div>' + item.description + '</div>'
                    },
                    click: function (marker) {
                        _this.onClickCallBack(item, marker);
                    }
                };
            };
            MapContainer.prototype.refresh = function () {
                var _this = this;
                this.data.forEach(function (item) {
                    _this.renderItem(item);
                });
            };
            MapContainer.prototype.clear = function () {
                this.data.forEach(function (item) {
                    MapContainer.clearTaskMarker(item);
                });
            };
            MapContainer.prototype.removeElement = function (item) {
                var i = this.data.indexOf(item);
                if (i > -1) {
                    this.data.splice(i, 1);
                    MapContainer.clearTaskMarker(item);
                }
            };
            MapContainer.clearTaskMarker = function (item) {
                if (item.marker) {
                    item.marker.setMap(null);
                    item.marker = null;
                }
            };
            MapContainer.prototype.showFiltered = function (fun) {
                var _this = this;
                this.data.forEach(function (element) {
                    if (fun(element)) {
                        _this.showItem(element);
                    }
                    else {
                        element.marker.setMap(null);
                    }
                });
            };
            MapContainer.prototype.showItem = function (element) {
                if (element.marker && !element.marker.getMap()) {
                    element.marker.setMap(this.gmap.map);
                }
            };
            MapContainer.prototype.showAll = function () {
                var _this = this;
                this.data.forEach(function (item) {
                    _this.showItem(item);
                });
            };
            MapContainer.prototype.addElement = function (item) {
                this.data.push(item);
                this.renderItem(item);
                $('#CreateTaskModal').modal('hide');
            };
            MapContainer.getIcon = function (item) {
                if (item.type == 'task') {
                    var segment = item.segment != null ? item.segment : 'Z';
                    if (item.status === 'complete') {
                        console.log('Complete Tasks');
                        console.log(item);
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
                    return 'http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|' + segment + '|' + iconFillColor + '|FFFFFF|ffff33';
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