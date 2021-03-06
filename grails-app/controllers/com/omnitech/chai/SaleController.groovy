package com.omnitech.chai

import com.omnitech.chai.model.Sale
import com.omnitech.chai.model.SalesCall
import com.omnitech.chai.model.Task
import org.springframework.security.access.AccessDeniedException

import static com.omnitech.chai.model.Role.SALES_ROLE_NAME
import static org.springframework.http.HttpStatus.FORBIDDEN

/**
 * Created by kay on 12/10/2014.
 */
class SaleController extends TaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        if(!params.sort){
            params.sort = 'completionDate'
            params.order = 'desc'
        }
        super.index max, Sale, [view: '/call/index', taskRole: SALES_ROLE_NAME]
    }

    def salesCall(Integer max){
        params.action = "salesCall"
        super.index max, SalesCall, [view: '/call/salescall', taskRole: SALES_ROLE_NAME]
    }
    
    def map(Integer max) {
        super.map max, Sale, [view: '/task/map']
    }

    def export() {
        super.export Sale
    }

    def search(Integer max) {
        super.search max, Sale, [view: '/call/index', taskRole: SALES_ROLE_NAME]
    }

    def searchMap(Integer max) {
        super.searchMap max, Sale, [view: '/task/map', taskRole: SALES_ROLE_NAME]
    }

    def show() {
        super.show view: '/call/show', taskRole: SALES_ROLE_NAME, viewParams: [noedit_menu: true]
    }

    def showCall() {
        super.show view: '/call/showCall', taskRole: SALES_ROLE_NAME, viewParams: [noedit_menu: true]
    }

    def handleException(AccessDeniedException ex) {
        render view: '/login/denied', status: FORBIDDEN
    }

    def delete() {
        super.delete()
    }


    def beforeInterceptor = {
        params.controller = 'visit'
        //params.status = Task.STATUS_COMPLETE
    }

}
