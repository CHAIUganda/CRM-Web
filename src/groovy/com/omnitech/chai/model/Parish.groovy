package com.omnitech.chai.model


class Parish {

    String id
    String name

    Date dateCreated
    Date lastUpdated

    @Override
    public String toString() {
        return "${name}";
    }
}
