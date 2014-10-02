package com.omnitech.chai.model

import grails.validation.Validateable
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
    String latLng

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
            numberOfProducts

    Double turnOver

    Date tenureStartDate
    Date tenureEndDate

    @RelatedTo(type = Relations.HAS_CONTACT)
    List<CustomerContact> customerContacts


    static constraints = {
        outletName                  blank: false
        outletType                  blank: false, inList: ['pharmacy','drug shop','clinic','health center','hospital']
        outletSize                  blank: false, inList: ['b', 'm', 's']
        split                       blank: false, inList: ['urban', 'rural']
        numberOfEmployees           nullable: false, min: 1
        openingHours                blank: false
        turnOver                    nullable: false, min: 1 as Double
        numberOfBranches            nullable: false, min: 1
        numberOfCustomersPerDay     nullable: false, min: 1
        majoritySourceOfSupply      blank: false, inList: ['district', 'trading center', 'whole saler']
        buildingStructure           blank: false, inList: ['permananet', 'semi-parmanent', 'non-permanent']
        equipment                   blank: false
        tenureEndDate               nullable: false
        tenureStartDate             nullable: false
        descriptionOfOutletLocation blank: false
    }


    void generateLatLng() {
        latLng = String.format("POINT(%.6f %.6f)", lng, lat)
    }

    void createWkt() {
        setLocation(lng, lat)
    }

    public void setLocation(float lng, float lat) {
        this.lat = lat
        this.lng = lng
        this.latLng = String.format("POINT( %.6f %.6f )", lng, lat);
    }

}