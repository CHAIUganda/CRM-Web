package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.support.index.IndexType

/**
 * Created by kay on 11/7/14.
 */
@NodeEntity
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

}
