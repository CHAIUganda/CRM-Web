declare module omnitech.chai {
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

}

declare var omnitechBase:string;