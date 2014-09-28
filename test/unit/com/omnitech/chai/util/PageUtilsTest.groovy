package com.omnitech.chai.util

import org.springframework.data.domain.Sort
import spock.lang.Specification

/**
 * Created by kay on 9/26/14.
 */
class PageUtilsTest extends Specification {

    void 'test the the correct page params are set'() {

        expect:
        def pg = PageUtils.create(params)
        pg.pageSize == max
        pg.offset == offset
        pg.sort?.getOrderFor('name')?.direction == direction

        where:
        params                                                | max | offset | direction
        [:]                                                   | 50  | 0      | null
        null                                                  | 50  | 0      | null
        [offset: 0, max: 10]                                  | 10  | 0      | null
        [offset: 50, max: 10]                                 | 10  | 50     | null
        [offset: -1, max: 10]                                 | 10  | 0      | null
        [offset: 50, max: -16]                                | 50  | 50     | null
        [offset: 50, max: -16]                                | 50  | 50     | null
        [offset: 50, max: -16, sort: 'name']                  | 50  | 50     | Sort.DEFAULT_DIRECTION
        [offset: 50, max: -16, sort: 'name', order: 'asc']    | 50  | 50     | Sort.DEFAULT_DIRECTION
        [offset: 50, max: -16, sort: 'name', order: 'desc']   | 50  | 50     | Sort.Direction.DESC
        [offset: 50, max: -16, sort: 'namezz', order: 'desc'] | 50  | 50     | null
        [offset: 30, max: -16, sort: 'name', order: 'asc']    | 50  | 0      | Sort.DEFAULT_DIRECTION
        [offset: 121, max: -16, sort: 'name', order: 'asc']   | 50  | 100    | Sort.DEFAULT_DIRECTION
        [offset: 121, max: 10, sort: 'name', order: 'asc']    | 10  | 120    | Sort.DEFAULT_DIRECTION

    }

}
