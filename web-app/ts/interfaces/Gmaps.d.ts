///<reference path='../libs/googlemaps/google.maps.d.ts'/>
declare class GMaps {
    constructor(options:GMapOptions)
    addMarker(options:GMarkerOptions):void
}

interface GMapOptions {
    div: string
    lat: number
    lng: number
    zoom?:number
    width?:number
    height?:number
}


interface GMarkerOptions {
    lat:number
    lng:number
    title: string
    click: (event:google.maps.MouseEvent)=>void
}

