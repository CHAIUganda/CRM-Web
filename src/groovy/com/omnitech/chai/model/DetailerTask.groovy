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
    Boolean doYouStockOrsZinc
    Integer howManyZincInStock
    Integer howmanyOrsInStock
    String zincBrandsold
    String orsBrandSold
    String ifNoWhy
    Double zincPrice
    Double orsPrice
    Double buyingPrice
    String pointOfsaleMaterial
    String recommendationNextStep
    String recommendationLevel

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
     String brand;
    /** Not-null value. */
     String category;
     double stockLevel;
     Double buyingPrice;
     Double sellingPrice;
}
