package com.omnitech.chai.util

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Created by kay on 9/24/14.
 */

class PageUtils {


    static int DEFAULT_PAGE_SIZE = 50

    static PageRequest create(Map params) {

        int start = (params['start'] ?: 0) as Integer
        int size = (params['size'] ?: DEFAULT_PAGE_SIZE) as Integer

        String[] sort
        if (params['sort'] instanceof Collection)
            sort = ((Collection) params['sort']).collect { it?.toString() } as String[]
        else if (params['sort'] instanceof String)
            sort = [params['sort']] as String[]


        Sort.Direction direction = 'desc' == params['order'] ? Sort.Direction.DESC : Sort.DEFAULT_DIRECTION



        if (sort)
            return new PageRequest(start, size, direction, sort)
        return new PageRequest(start, size)
    }

}
