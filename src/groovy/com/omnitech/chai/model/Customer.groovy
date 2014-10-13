package com.omnitech.chai.model

import grails.validation.Validateable
import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.list.LazyList
import org.springframework.data.annotation.Transient
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.support.index.IndexType

/**
 * Created by kay on 9/21/14.
 */
@NodeEntity
@Validateable
public class Customer extends AbstractEntity {

    Float lat
    Float lng

    @Indexed(indexType = IndexType.POINT, indexName = 'CUSTOMER_LOCATION')
    String wkt

    String outletName,
           outletType,
           outletSize,
           split,
           openingHours,
           majoritySourceOfSupply,
           keyWholeSalerName,
           keyWholeSalerContact,
           buildingStructure,
           equipment,
           descriptionOfOutletLocation


    Integer numberOfEmployees,
            numberOfBranches,
            numberOfCustomersPerDay,
            numberOfProducts,
            restockFrequency

    Double turnOver

    Date tenureStartDate
    Date tenureEndDate

    @Fetch
    @RelatedTo(type = Relations.HAS_CONTACT)
    Set<CustomerContact> customerContacts

    @Fetch
    @RelatedTo(type = Relations.BELONGS_TO_SC)
    SubCounty subCounty

    @Fetch
    @RelatedTo(type = Relations.CUST_IN_TERRITORY)
    Territory territory

    //this is mainly used for automatic data binding of dynamic contacts
    @Transient
    List<CustomerContact> tCustomerContacts = LazyList.decorate([], FactoryUtils.constantFactory(CustomerContact))


    static constraints = {
        outletName blank: false
        outletType blank: false, inList: ['pharmacy', 'drug shop', 'clinic', 'health center', 'hospital']
        outletSize blank: false, inList: ['b', 'm', 's']
        split blank: false, inList: ['urban', 'rural']
        numberOfEmployees nullable: false, min: 1
        openingHours blank: false
        turnOver nullable: false, min: 1 as Double
        numberOfBranches nullable: false, min: 1
        numberOfCustomersPerDay nullable: false, min: 1
        majoritySourceOfSupply blank: false, inList: ['district', 'trading center', 'whole saler']
        buildingStructure blank: false, inList: ['permananet', 'semi-parmanent', 'non-permanent']
        equipment blank: false
        tenureEndDate nullable: false
        tenureStartDate nullable: false
        descriptionOfOutletLocation blank: false
        restockFrequency nullable: false, min: 1
        subCounty nullable: false
    }


    void createWkt() {
        setLocation(lng, lat)
    }

    List<CustomerContact> copyToContacts2LazyList() {
        if (customerContacts)
            tCustomerContacts.addAll(customerContacts)
        return tCustomerContacts
    }

    public void setLocation(Float lng, Float lat) {
        this.lat = lat
        this.lng = lng
        if (lat && lng)
            this.wkt = String.format("POINT( %.6f %.6f )", lng, lat);
        else
            this.wkt = null
    }


    def beforeSave() {
        createWkt()
        if (tCustomerContacts)
            customerContacts = new HashSet<>(tCustomerContacts)
    }

}