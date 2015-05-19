package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo


/**
 * Created by kay on 11/7/14.
 */
@NodeEntity
@Validateable
class MalariaDetails extends Task {
    Integer dateOfSurvey
    String doYouKnowMOHGuidelines
    String doYouPrescribeTreatment
    String doYouPrescribeWithoutGreenLeaf
    String doYouStockAntimalarials
    String doYouStockRDTs
    String heardAboutGreenLeaf
    String howDidYouHear
    String howToManagePatientsWithSevereMalaria

    String howYouSuspectMalaria
    String isHistory
    String isNew
    String knowAboutGreenLeafAntimalarials
    String knowWhatSevereMalariaIs
    Integer latitude
    Integer longitude
    Integer malariaPatientsInFacility
    String mohGuidelines
    
    Integer numberOfChildren
    String otherWaysHowYouHeard
    String pointOfsaleMaterial
    String recommendationNextStep
    String signsOfSevereMalaria
    String whatGreenLeafRepresents
    String whyPrescribeWithoutGreenLeaf
    
    @Fetch
    @RelatedTo(type = Relations.HAS_DETAILER_STOCK)
    Set<DetailerMalariaStock> detailerMalariaStocks

    def beforeSave() {
        super.beforeSave()
        if (!description)
            description = "Detailing [$customer.outletName]"
    }

    static create(Customer c, Date d) {
        new MalariaDetails(customer: c, description: "Detailing [$c.outletName]", dueDate: d)
    }
}

@NodeEntity
class DetailerMalariaStock extends AbstractEntity {
     String brand;
    /** Not-null value. */
    String category;
    double stockLevel;
    Double buyingPrice;
    Double sellingPrice;
    String detailerId;
    String malariadetailId;
}
