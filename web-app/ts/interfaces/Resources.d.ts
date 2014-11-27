declare module omnitech.chai {

    interface HasError {
        error:string;
    }

    interface District {
        id: number;
        name : string;
    }

    interface Territory extends District {
    }

    interface SubCounty extends District {
        mapped: boolean;
        territory: string;
    }

    interface HttPromise {
        success(func:() => void):HttPromise ;
        error (func:(data:string) => void):HttPromise;
    }

    interface HasCoords {
        lat: string
        lng:string
    }

}


interface MarkerWithLabelOptions extends google.maps.MarkerOptions {
    labelContent ?: string;
    labelAnchor ?: google.maps.Point;
    labelClass ?:  string;
    labelStyle ?: any;
}


declare class MarkerWithLabel extends google.maps.Marker {
    constructor(option:MarkerWithLabelOptions);
}

declare var krcBase:string;
declare var krcConfigs:any;

declare var omnitechBase:string;