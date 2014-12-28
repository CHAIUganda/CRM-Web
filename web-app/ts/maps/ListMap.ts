///<reference path='../_all.ts'/>

module omnitech.chai {


    export class MapContainer {

        latLngBounds = new google.maps.LatLngBounds();
        public gmap:GMaps;

        constructor(private data:Task[], private onClickCallBack:(Task, MarkerWithLabel)=> void) {
            this.createMap();

        }

        private createMap() {
            this.gmap = new GMaps({lat: 1.354255, lng: 32.314228, div: "#map",zoom : 3});
            this.data.forEach((item)=> {
                if (item.lat && item.lng) {
                    this.gmap.addMarker({
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

                    this.latLngBounds.extend(new google.maps.LatLng(item.lat, item.lng))
                }
            });

            this.gmap.map.fitBounds(this.latLngBounds);
        }

        static getMapIconOptions(item:Task):any {
            return {
                path: google.maps.SymbolPath.CIRCLE,
                scale: 4,
                fillColor: MapContainer.getColor(item.dueDays),
                strokeColor: MapContainer.getColor(item.dueDays)
            };
        }

        static getColor(days:number):string {
            //http://colorbrewer2.org/
            if (days == 1) return '#e41a1c';
            if (days == 2) return '#377eb8';
            if (days == 3) return '#4daf4a';
            if (days == 4) return '#984ea3';
            if (days >= 30) return '#ff7f00';
            if (days >= 14) return '#ffff33';
            if (days >= 11) return '#a65628';
            if (days >= 8) return '#f781bf';
            if (days >= 5) return '#999999';
        }

    }


}