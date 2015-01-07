package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import com.omnitech.chai.util.LeafNode
import grails.validation.Validateable
import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.list.LazyList
import org.springframework.data.annotation.Transient
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.support.index.IndexType
import org.springframework.validation.FieldError

/**
 * Created by kay on 9/21/14.
 */
@NodeEntity
@Validateable
public class Customer extends AbstractEntity implements LeafNode {

    final static TYPE_CLINIC = 'clinic'
    final static TYPE_PHARMACY = 'pharmacy'
    final static TYPE_DRUG_SHOP = 'drug shop'
    final static TYPE_HOSPITAL = 'hospital'
    final static TYPE_HEALTH_CENTER = 'health center'
    final static STRUCT_SEMI_PERMANENT = 'semi-permanent'
    final static STRUCT_PERMANENT = 'permanent'
    final static STRUCT_NON_PERMANENT = 'non-permanent'

    Float lat
    Float lng
    Double segmentScore

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
           descriptionOfOutletLocation,
           numberOfProducts,
           pictureURL,
           tradingCenter,
           visibleEquipment,
           turnOver


    Integer numberOfEmployees,
            numberOfCustomersPerDay,
            restockFrequency,
            childrenUnder5yrs

    //todo add hasSisterBranch
    Boolean hasSisterBranch
    //todo add dateOutletOpened
    Date dateOutletOpened


    @Fetch
    @RelatedTo(type = Relations.HAS_CONTACT)
    Set<CustomerContact> customerContacts

    @Fetch
    @RelatedTo(type = Relations.CUST_IN_SC)
    SubCounty subCounty

    @Fetch
    @RelatedTo(type = Relations.CUST_IN_PARISH)
    Parish parish

    @Fetch
    @RelatedTo(type = Relations.CUST_IN_VILLAGE)
    Village village


    @Fetch
    @RelatedTo(type = Relations.IN_SEGMENT)
    CustomerSegment segment

    //this is mainly used for automatic data binding of dynamic contacts
    @Transient
    List<CustomerContact> tCustomerContacts = LazyList.decorate([], FactoryUtils.constantFactory(CustomerContact))


    static constraints = {
        outletName blank: false
        outletType blank: false, inList: [TYPE_PHARMACY, TYPE_DRUG_SHOP, TYPE_CLINIC, TYPE_HEALTH_CENTER, TYPE_HOSPITAL]
        outletSize blank: false, inList: ['big', 'medium', 'small']
        typeOfLicence blank: false, inList: ['National Drug Authority', 'Pharmaceutical Society of Uganda', 'Ugandan Medical and Dental Practitioners', 'Ministry of Health', 'Unlicensed', 'Others']
        split blank: false, inList: ['urban', 'rural']
        numberOfEmployees nullable: false, min: 0, max: 30
//        openingHours blank: false, inList: ['early morning', 'late morning', 'noon', 'early afternoon', 'late afternoon', 'evening']

//        turnOver nullable: false, inList: ['less than 50,000 UGX', '50,000-150,000 UGX', '150,000 - 300,000 UGX', 'greater than 300,000 UGX']
        numberOfCustomersPerDay nullable: false, min: 1
        majoritySourceOfSupply blank: false
        buildingStructure blank: false, inList: [STRUCT_PERMANENT, STRUCT_SEMI_PERMANENT, STRUCT_NON_PERMANENT]
        descriptionOfOutletLocation blank: false
        restockFrequency nullable: false, min: 1
        numberOfProducts nullable: false, inList: ['less than 10', '10-30', 'more than 30']

        tCustomerContacts validator: { val, obj ->
            // 'attributes.validation.failed' is the key for the message that will
            // be shown if validation of innerCommands fails
            return val.every { it.validate() } ?: ['tCustomerContacts.validation.failed']
        }
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

    void runValidation() {
        if (!tCustomerContacts) return

        tCustomerContacts?.each { customer ->
            customer.validate()
            if (customer.hasErrors()) {
                customer.errors.allErrors.each { FieldError error ->
                    final String field = error.field?.replace('profile.', '')
                    final String code = "registrationCommand.$field.$error.code"
                    errors.rejectValue(field, code)
                }
            }

        }
    }

    String toString() { outletName }

    def beforeSave() {
        createWkt()
        if (tCustomerContacts)
            customerContacts = new HashSet<>(tCustomerContacts)
    }

    boolean isClinic(String outLetType) {
        ['pharmacy', 'drug shop', 'clinic', 'health center', 'hospital']
        false
    }

    @Override
    GroupNode getParent() {
        return segment
    }

    @Override
    String getName() {
        return outletName
    }

    CustomerContact keyContact() {
        customerContacts?.find {}
    }
}