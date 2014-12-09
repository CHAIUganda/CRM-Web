package com.omnitech.chai.model

/**
 * Created by kay on 9/29/14.
 */
interface Relations {

    //District -> SubCounty
    final static String HAS_SUB_COUNTY = 'HAS_SUB_COUNTY'

    //SubCount -> Parish
    final static String HAS_PARISH = 'HAS_PARISH'

    //Parish -> Village
    final static String HAS_VILLAGE = 'HAS_VILLAGE'

    //Customer -> Territory
    final static String CUST_IN_TERRITORY = 'CUST_IN_TERRITORY'

    //Region -> District
    final static String HAS_DISTRICT = 'HAS_DISTRICT'

    //Customer -> Contact
    final static String HAS_CONTACT = 'HAS_CONTACT'

    //Customer -> SubCounty
    final static String BELONGS_TO_SC = 'BELONGS_TO_SC'

    //Customer -> Village
    final static String CUST_IN_VILLAGE = 'CUST_IN_VILLAGE'

    //Customer -> Village
    final static String CUST_IN_PARISH = 'CUST_IN_PARISH'

    //Customer -> Segment
    final static String IN_SEGMENT = 'IN_SEGMENT'


    //Subcounty -> Territory
    final static String SC_IN_TERRITORY = 'SC_IN_TERRITORY'

    //User -> Task
    final static ASSIGNED_TASK = 'ASSIGNED_TASK'

    // User -> Territory
    final static USER_TERRITORY = 'USER_TERRITORY'

    //User -> Task
    final static COMPLETED_TASK = 'COMPLETED_TASK'

    //Task -> Customer
    final static CUST_TASK = 'CUST_TASK'

    //ProductGroup -> Product
    final static GRP_HAS_PRD = 'GRP_HAS_PRD'

    //Group -> Group
    final static GRP_HAS_GRP = 'GRP_HAS_GRP'


}
