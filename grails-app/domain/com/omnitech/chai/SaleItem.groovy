package com.omnitech.chai

class SaleItem {

    String id

    Product product
    Promotion promotion

    Double amountPaid


    Date dateCreated
    Date lastUpdated

    static belongsTo = [sale: Sale]

    static mapping = {
        id(generator: "com.omnitech.mis.utils.MyIdGenerator", type: "string", length: 32)
    }

    static constraints = {
    }


    @Override
    public String toString() {
        return "${product.name}";
    }
}
