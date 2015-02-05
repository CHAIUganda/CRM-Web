///<reference path='../_all.ts'/>

module omnitech.chai {


    export class MapContainer {

        latLngBounds = new google.maps.LatLngBounds();
        public gmap:GMaps;

        constructor(private data:Task[], private onClickCallBack:(Task, MarkerWithLabel)=> void) {
            this.createMap();

        }

        private createMap() {
            this.gmap = new GMaps({lat: 1.354255, lng: 32.314228, div: "#map", zoom: 7});

            this.refresh();

            setTimeout(()=> {
                this.gmap.map.fitBounds(this.latLngBounds);
            }, 3000);


            this.gmap.map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(document.getElementById('legend'));
            this.gmap.map.controls[google.maps.ControlPosition.TOP_CENTER].push(document.getElementById('showCustomers'))
        }

        renderItem(item:Task):void {
            if (item.lat && item.lng) {
                //MapContainer.clearTaskMarker(item);
                if (item.marker) {
                    item.marker.setOptions(MapContainer.getMapIconOptions(item))
                } else {
                    item.marker = this.gmap.addMarker({
                        lat: item.lat,
                        lng: item.lng,
                        icon: MapContainer.getMapIconOptions(item),
                        infoWindow: {
                            content: '<div>' + item.description + '</div>'
                        },
                        click: (marker)=> {
                            this.onClickCallBack(item, marker);
                        }
                    });
                }
                this.latLngBounds.extend(new google.maps.LatLng(item.lat, item.lng))
            }
        }

        refresh() {
            this.data.forEach((item)=> {
                this.renderItem(item);
            });
        }


        clear() {
            this.data.forEach((item)=> {
                MapContainer.clearTaskMarker(item);
            });
        }

        removeElement(item:Task):void {
            var i = this.data.indexOf(item);
            if (i > -1) {
                this.data.splice(i, 1);
                MapContainer.clearTaskMarker(item);
            }
        }

        private static clearTaskMarker(item) {
            if (item.marker) {
                item.marker.setMap(null);
                item.marker = null;
            }
        }

        showFiltered(fun:(item:Task) => boolean) {
            this.data.forEach((element) => {
                if (fun(element)) {
                    this.showItem(element);
                } else {
                    element.marker.setMap(null);
                }
            });
        }

        private showItem(element) {
            if (element.marker && !element.marker.getMap()) {
                element.marker.setMap(this.gmap.map);
            }
        }

        showAll() {
            this.data.forEach((item)=> {
                this.showItem(item)
            });
        }

        addElement(item:Task):void {
            this.data.push(item);
            this.renderItem(item);
            $('#CreateTaskModal').modal('hide')
        }

        static getMapIconOptions(item:Task):any {
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
            } else {
                var segment = item.segment != null ? item.segment : 'Z';
                var iconFillColor = '4B0082';
                return 'http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|' + segment + '|' + iconFillColor + '|FFFFFF|ffff33';
            }
        }

        static getColor(days:number):string {
            //http://colorbrewer2.org/
            if (days < -1) return '#e41a1c';
            if (days === -1) return '#377eb8';
            if (days === 0) return '#4daf4a';
            if (days === 1) return '#984ea3';
            if (days === 2) return '#ff7f00';
            if (days === 3) return '#ffff33';
            if (days === 4) return '#a65628';
            if (days === 5) return '#f781bf';
            if (days === 6) return '#999999';
            if (days >= 7) return '#000000';
        }

    }


}