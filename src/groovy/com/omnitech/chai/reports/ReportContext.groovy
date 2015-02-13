package com.omnitech.chai.reports

import com.omnitech.chai.model.Product
import com.omnitech.chai.model.Territory
import com.omnitech.chai.model.User

/**
 * Created by kay on 2/12/2015.
 */
interface ReportContext {

    List<Product> getUserProducts()

    List<Territory> getUserTerritories()

    List<User> getSupervisedUsers()

}
