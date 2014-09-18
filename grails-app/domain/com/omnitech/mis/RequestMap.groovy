package com.omnitech.mis

import org.springframework.http.HttpMethod

class RequestMap {


    String id
    String url
    String configAttribute
    HttpMethod httpMethod

    static auditable = true

    static mapping = {
        id(generator: "com.omnitech.mis.utils.MyIdGenerator", type: "string", length: 32, class: String)
        cache true
    }

    static constraints = {
        url blank: false, unique: 'httpMethod'
        configAttribute blank: false
        httpMethod nullable: true
    }
}
