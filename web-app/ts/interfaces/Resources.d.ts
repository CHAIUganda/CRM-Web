declare module omnitech.chai {
    interface District {
        id: number;
        name : string;
    }

    interface Territory extends District {
    }

    interface SubCounty extends District {
        mapped: boolean;
    }

}

declare var omnitechBase:string;