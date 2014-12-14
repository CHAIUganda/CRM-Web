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
        name ?: string;
        unitOfMeasure ?: string;
        formulation ?: string;
        unitPrice ?: string;
    }

    interface LineItem {
        order  ?: Order;
        product ?: Product;
        quantity ?: number;
        unitPrice  ?: number;
    }

    interface Order {
        customer ?: Customer;
        lineItems ?: LineItem[];
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