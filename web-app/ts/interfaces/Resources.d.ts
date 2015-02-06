declare module omnitech.chai {

    interface HasError {
        error:string;
    }

    interface District {
        id: number;
        name : string;
    }

    interface Territory extends District {
        id: number
        name: string
        mapped: boolean
    }

    interface SubCounty extends District {
        mapped: boolean;
        territory: string;
    }

    interface Customer extends HasCoords{
        id : number;
        outletName: string;
        contact: string;
        district: string;
        segment?: string
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
        id ?: number;
        activeLineItem ?: LineItem;
        customer ?: Customer;
        lineItems ?: Array<LineItem>;
        comment ?: string;
    }

    interface HttPromise {
        success(func:(msg?:any) => void):HttPromise ;
        error (func:(data:string) => void):HttPromise;
    }

    interface HasCoords {
        lat: number
        lng: number
        description: string
        marker?:google.maps.Marker
    }


    interface Task extends HasCoords {
        id ?: string
        dueDate?:Date
        dueDateText?:string
        completionDate?:Date
        systemDueDate?:Date
        assignedTo?:string
        completedBy?:string
        wkt?:string
        segment?: string
        assignedUser?:string
        customer?: string
        customerDescription?:string
        type: string
        status?:string
        customerId:number
        dueDays?: number
        title: string
    }

    interface CallModel {
        customer: Customer
        description: string
        dueDate : string
    }

    interface User {
        id : number
        username: string

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
declare var chaiMapData:omnitech.chai.Task[];
declare var _products:omnitech.chai.Product[];
declare var _orderCustomer:omnitech.chai.Customer;