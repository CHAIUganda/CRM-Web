package com.omnitech.chai.service

import com.omnitech.chai.model.User
import org.springframework.data.neo4j.lifecycle.BeforeSaveEvent
import spock.lang.Specification

/**
 * Created by kay on 9/29/14.
 */
class PersistentHelperTest extends Specification {

    def pHelper

    def setup() {
        pHelper = new PersistentHelper()
    }

    def "OnApplicationEvent"() {

        given:
        User u = new User()
        BeforeSaveEvent bse = new BeforeSaveEvent(this, u)

        when: 'both id and uuid is null'
        u.id = null
        u.uuid = null
        pHelper.onApplicationEvent(bse)
        then: 'uuid should be set'
        u.uuid.count('-') == 4

        when: 'uuid and id is set'
        u.id = 5
        u.uuid = 'xxxxx'
        pHelper.onApplicationEvent(bse)
        then: 'uuid should not be altered'
        u.uuid == 'xxxxx'

        when: 'id is null and uuid is set'
        u.id = null
        u.uuid = 'xxxxx'
        pHelper.onApplicationEvent(bse)
        then: 'uuid should be set. uuids should be set by only our system'
        u.uuid.count('-') == 4

        when: 'id is set and uuid is null'
        u.id = 5
        u.uuid = null
        pHelper.onApplicationEvent(bse)
        then: 'uuid should be set'
        u.uuid.count('-') == 4

    }

}
