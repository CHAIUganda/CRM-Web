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
           typeOfLicence,
           split,
           openingHours,

           majoritySourceOfSupply,
           keyWholeSalerName,
           keyWholeSalerContact,
           buildingStructure,
           equipment,
           descriptionOfOutletLocation,
           numberOfProducts,
           pictureURL


    Integer numberOfEmployees,
            numberOfBranches,
            numberOfCustomersPerDay,
            restockFrequency,
            tenureLength

    String turnOver

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
        outletSize blank: false, inList: ['big', 'medium', 'small']
        typeOfLicence blank: false, inList: ['National Drug Authority', 'Pharmaceutical Society of Uganda', 'Ugandan Medical and Dental Practitioners', 'Ministry of Health', 'Unlicensed', 'Others']
        split blank: false, inList: ['urban', 'rural']
        numberOfEmployees nullable: false, min: 0,max: 30
        openingHours blank: false, inList: ['early morning', 'late morning', 'noon', 'early afternoon', 'late afternoon', 'evening']

        turnOver nullable: false, inList: ['less than 50,000 UGX', '50,000-150,000 UGX', '150,000 - 300,000 UGX', 'greater than 300,000 UGX']
        numberOfBranches nullable: false, min: 1
        numberOfCustomersPerDay nullable: false, min: 1
        majoritySourceOfSupply blank: false
        buildingStructure blank: false, inList: ['permanent', 'semi-permanent', 'non-permanent']
        equipment blank: false
        tenureLength min: 0, max: 20
        descriptionOfOutletLocation blank: false
        restockFrequency nullable: false, min: 1
        subCounty nullable: false
        numberOfProducts nullable: false, inList: ['less than 10', '10-30', 'more than 30']
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

    String toString() { outletName }

    def beforeSave() {
        createWkt()
        if (tCustomerContacts)
            customerContacts = new HashSet<>(tCustomerContacts)
    }

}