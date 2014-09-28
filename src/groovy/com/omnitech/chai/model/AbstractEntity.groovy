package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.GraphId

/**
 * Created by kay on 9/28/14.
 */

class AbstractEntity {
    @GraphId
    Long id
    String uuid
}
