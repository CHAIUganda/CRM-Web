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

    interface Customer {
        outletName: string;
        contact: string;
        district: string;
    }

    interface Product {
        id?:number;
        name ?: string;
        unitOfMeasure ?: string;
        formulation ?: string;
        unitPrice ?: number;
    }

    interface LineItem {
        productId ?: number;
        product ?: Product;
        quantity ?: number;
        unitPrice  ?: number;
    }

    interface Order {
        activeLineItem ?: LineItem;
        customer ?: Customer;
        lineItems ?: LineItem[];
        comment ?: string;
    }

    interface HttPromise {
        success(func:() => void):HttPromise ;
        error (func:(data:string) => void):HttPromise;
    }

    interface HasCoords {
        lat: number
        lng: number
        title: string
        description: string
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

declare var omnitechBase:string;
declare var chaiMapData:omnitech.chai.HasCoords[];
declare var _products:omnitech.chai.Product[];