///<reference path='../libs/googlemaps/google.maps.d.ts'/>
declare class GMaps {
    constructor(options:GMapOptions);
    addMarker(options:GMarkerOptions):google.maps.Marker;
    public map:google.maps.Map;
}

interface GMapOptions {
    div: string
    lat?: number
    lng?: number
    zoom?:number
    width?:number
    height?:number
}


interface GMarkerOptions {
    lat:number
    lng:number
    title?: string
    click?: (event:MarkerWithLabel)=>void
}

