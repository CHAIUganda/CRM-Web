package com.omnitech.chai.model

import grails.validation.Validateable
import org.grails.datastore.mapping.annotation.Index
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo


/**
 * Created by kay on 11/7/14.
 */
@NodeEntity
@Validateable
class DetailerTask extends Task {
    Integer diarrheaPatientsInFacility
    String heardAboutDiarrheaTreatmentInChildren
    String howDidYouHear
    String otherWaysHowYouHeard
    String whatYouKnowAbtDiarrhea
    String diarrheaEffectsOnBody
    String knowledgeAbtOrsAndUsage
    String knowledgeAbtZincAndUsage
    String whyNotUseAntibiotics
    String pointOfsaleMaterial
    String recommendationNextStep
    String recommendationLevel
    String objections

    //new items
    String ifNoZincWhy;
    String ifNoOrsWhy;

    @Fetch
    @RelatedTo(type = Relations.HAS_DETAILER_STOCK)
    Set<DetailerStock> detailerStocks


    def beforeSave() {
        super.beforeSave()
        if (!description)
            description = "Detailing [$customer.outletName]"
    }

    static create(Customer c, Date d) {
        new DetailerTask(customer: c, description: "Detailing [$c.outletName]", dueDate: d)
    }

}

@NodeEntity
class DetailerStock extends AbstractEntity {
    @Index
    String brand;
    /** Not-null value. */
    @Index
    String category;
    double stockLevel;
    Double buyingPrice;
    Double sellingPrice;
}
