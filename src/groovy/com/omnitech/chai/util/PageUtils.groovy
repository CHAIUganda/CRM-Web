package com.omnitech.chai.util

import org.neo4j.cypherdsl.Order
import org.neo4j.cypherdsl.grammar.OrderBy
import org.neo4j.cypherdsl.grammar.ReturnNext
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

import static org.neo4j.cypherdsl.CypherQuery.identifier
import static org.neo4j.cypherdsl.CypherQuery.order

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

    static <T extends OrderBy> T  addPagination(T next, Map param, Class returnClass) {
        def pageParams = create(param)
        addSorting(next, param, returnClass)
                .skip(pageParams.offset)
                .limit(pageParams.pageSize)
        return next
    }

    /**
     * Use this instead of addPagination if u intend to use CypherDSLRepo
     */
    static <T extends OrderBy> T addSorting(T next, Map param, Class returnClass) {
        def entityName = returnClass.simpleName.toLowerCase()

        def pageParams = create(param)
        def sortIt = pageParams.sort?.iterator()

        String sort
        Order direction = Order.ASCENDING

        if (sortIt?.hasNext()) {
            def order = sortIt.next()
            sort = order.property
            if (sort?.contains('.')) {
                (entityName,sort) = sort.split(/\./)

            }
            direction = order.direction == Sort.Direction.ASC ? Order.ASCENDING : Order.DESCENDING
        }
        next.orderBy(order(identifier(entityName).property(sort ? sort : 'id'), direction))
        return next
    }

}
