///<reference path='../_all.ts'/>

module omnitech.chai {


    class MapContainer {

        public gmap:GMaps;

        constructor(private data?:HasCoords[]) {
            this.createMap();
        }

        private createMap() {
            this.gmap = new GMaps({lat: 1.354255, lng: 32.314228, div: "#map", zoom: 7});
            this.data.forEach((item)=> {
                if (item.lat && item.lng)
//                    title: item.description,
                    this.gmap.addMarker({
                        lat: item.lat,
                        lng: item.lng,
                        icon : "http://labs.google.com/ridefinder/images/mm_20_blue.png" ,
                        infoWindow: {
                            content: '<p>'+item.description+'</p>'
                        }
                    });
            });
        }

    }

    var cont = new MapContainer(chaiMapData);
}