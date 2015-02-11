package com.omnitech.chai.model

/**
 * Created by kay on 9/29/14.
 */
interface Relations {

    //District -> SubCounty
    final static String HAS_SUB_COUNTY = 'HAS_SUB_COUNTY'

    //Region -> District
    final static String HAS_DISTRICT = 'HAS_DISTRICT'

    //Customer -> Contact
    final static String HAS_CONTACT = 'HAS_CONTACT'

    //Customer -> SubCounty
    final static String CUST_IN_SC = 'CUST_IN_SC'

    //Customer -> Stock
    final static String STOCK_PRODUCT = 'STOCK_PRODUCT'


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

    // USer -> Manages Territory
    final static SUPERVISES_TERRITORY = 'SUPERVISES_TERRITORY'

    //User -> Task
    final static COMPLETED_TASK = 'COMPLETED_TASK'

    // User -> Role
    final static HAS_ROLE = 'HAS_ROLE'

    //Task -> Customer
    final static CUST_TASK = 'CUST_TASK'

    //ProductGroup -> Product
    final static GRP_HAS_PRD = 'GRP_HAS_PRD'

    final static PROD_IN_TERRITORY = 'PROD_IN_TERRITORY'

    //Group -> Group
    final static GRP_HAS_GRP = 'GRP_HAS_GRP'

    //WholeSaler -> SubCounty
    final static WHOLE_SALER_SC = 'WHOLE_SALER_SC'

    final static REPORT_GRP_REPORT = 'REPORT_GRP_REPORT'

    final static REPORT_GRP_GRP = 'REPORT_GRP_GRP'

    // Order -> User
    final static ORDER_TAKEN_BY = 'ORDER_TAKEN_BY'


}
