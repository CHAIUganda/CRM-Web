package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.Product
import com.omnitech.chai.model.Territory
import com.omnitech.chai.model.User
import com.omnitech.chai.reports.ReportContext

import static com.omnitech.chai.model.Role.*

/**
 * Created by kay on 2/12/2015.
 */
class ReportContextService implements ReportContext {


    def productService
    def regionService
    def neoSecurityService
    def userService
    def customerRepository

    @Override
    List<Product> getUserProducts() {
        def user = currentUser
        if (user.hasRole(ADMIN_ROLE_NAME, SUPER_ADMIN_ROLE_NAME))
            return productService.listAllProducts()
        return productService.findAllProductsForUser(currentUser)
    }

    @Override
    List<Territory> getUserTerritories() {
        def user = currentUser
        if (user.hasRole(ADMIN_ROLE_NAME, SUPER_ADMIN_ROLE_NAME))
            return regionService.listAllTerritorys()
        return regionService.findTerritoriesForUser(user, [max: 2000]).content
    }

    @Override
    List<User> getSupervisedUsers() {
        def user = currentUser
        if (user.hasRole(ADMIN_ROLE_NAME, SUPER_ADMIN_ROLE_NAME))
            return userService.listAllUsers(Collections.EMPTY_MAP)
        if (user.hasRole(DETAILING_SUPERVISOR_ROLE_NAME))
            return userService.listUsersSupervisedBy(currentUser.id, DETAILER_ROLE_NAME).content
        if (user.hasRole(SALES_SUPERVISOR_ROLE_NAME))
            return userService.listUsersSupervisedBy(currentUser.id, SALES_ROLE_NAME).content
        return [currentUser]
    }

    User getCurrentUser() {
        neoSecurityService.currentUser
    }

    @Override
    Double averageSalesValue(Customer customer) {
        return customerRepository.averageSalesValue(customer.id)
    }
}
