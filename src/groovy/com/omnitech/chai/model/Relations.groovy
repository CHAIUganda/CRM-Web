package com.omnitech.chai.model

/**
 * Created by kay on 9/29/14.
 */
interface Relations {

    final static String HAS_SUB_COUNTY = 'HAS_SUB_COUNTY'
    final static String HAS_PARISH = 'HAS_PARISH'
    final static String HAS_VILLAGE = 'HAS_VILLAGE'
    final static String HAS_CUSTOMER = 'HAS_CUSTOMER'
    final static String HAS_DISTRICT = 'HAS_DISTRICT'
    //Customer -> Interaction
    final static String HAS_INTERACTION = 'HAS_INTERACTION'
    final static String HAS_TERRITORY = 'HAS_TERRITORY'
    //Customer -> Contact
    final static String HAS_CONTACT = 'HAS_CONTACT'
    //Customer -> SubCounty
    final static String BELONGS_TO_SC = 'BELONGS_TO_SC'

}
