package com.omnitech.chai.util

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Created by kay on 9/24/14.
 */

class PageUtils {


    static int DEFAULT_PAGE_SIZE = 50

    /**
     * Create a spring data page request from the grails paginate params.
     */
    static PageRequest create(Map params) {
        params = params ?: [:]
        int offset = (params['offset'] ?: 0) as Integer
        int size = (params['max'] ?: DEFAULT_PAGE_SIZE) as Integer

        offset = offset < 0 ? 0 : offset
        size = size <= 0 ? DEFAULT_PAGE_SIZE : size


        int pageNumber = offset / size

        String[] sort
        if (params['sort'] instanceof Collection)
            sort = ((Collection) params['sort']).collect { it?.toString() } as String[]
        else if (params['sort'] instanceof String)
            sort = [params['sort']] as String[]


        Sort.Direction direction = 'desc' == params['order'] ? Sort.Direction.DESC : Sort.DEFAULT_DIRECTION



        if (sort)
            return new PageRequest(pageNumber, size, direction, sort)
        return new PageRequest(pageNumber, size)
    }

}
