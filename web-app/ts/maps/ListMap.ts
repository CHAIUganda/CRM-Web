///<reference path='../_all.ts'/>

module omnitech.chai {


    class MapContainer {

        public gmap:GMaps;

        constructor(private data?:HasCoords[]) {
            this.createMap();
        }

        private createMap() {
            this.gmap = new GMaps({lat: 0.639978, lng: 30.2308269, div: "#map",zoom: 7});

        }

    }

    var cont = new MapContainer();
}