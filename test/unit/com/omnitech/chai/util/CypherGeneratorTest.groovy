package com.omnitech.chai.util

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.District
import com.omnitech.chai.model.Region
import com.omnitech.chai.model.SubCounty
import spock.lang.Specification

/**
 * Created by kay on 10/7/14.
 */
class CypherGeneratorTest extends Specification {

    def 'test get Assoc arrow'() {

        def field = ReflectFunctions.findAllFields(Customer).find { it.type == SubCounty }

        when:
        def arrow = CypherGenerator.getAssocArrow(field)

        then:
        arrow == '-->'

        when:
        field = ReflectFunctions.findAllFields(District).find { it.type == Region }
        arrow = ModelFunctions.getAssocArrow(field)

        then:
        arrow == '<--'
    }

    def 'test generation'() {
        when:
        def query = CypherGenerator.getMatchStatement2(Customer)

        then:
        notThrown(Exception)
        query == "MATCH (customer:Customer)\n" +
                "where str(customer.lat) =~ '.*t.*' or str(customer.lng) =~ '.*t.*' or customer.wkt =~ '.*t.*' or customer.outletName =~ '.*t.*' or customer.outletType =~ '.*t.*' or customer.outletSize =~ '.*t.*' or customer.split =~ '.*t.*' or customer.openingHours =~ '.*t.*' or customer.majoritySourceOfSupply =~ '.*t.*' or customer.keyWholeSalerName =~ '.*t.*' or customer.keyWholeSalerContact =~ '.*t.*' or customer.buildingStructure =~ '.*t.*' or customer.equipment =~ '.*t.*' or customer.descriptionOfOutletLocation =~ '.*t.*' or str(customer.numberOfEmployees) =~ '.*t.*' or str(customer.numberOfBranches) =~ '.*t.*' or str(customer.numberOfCustomersPerDay) =~ '.*t.*' or str(customer.numberOfProducts) =~ '.*t.*' or str(customer.restockFrequency) =~ '.*t.*' or str(customer.turnOver) =~ '.*t.*' or str(customer.id) =~ '.*t.*' or customer.uuid =~ '.*t.*'\n" +
                " optional match (customer)-->(subcounty:SubCounty)\n" +
                "where subcounty.name =~ '.*t.*' or str(subcounty.id) =~ '.*t.*' or subcounty.uuid =~ '.*t.*'\n" +
                " optional match (subcounty)<--(district:District)\n" +
                "where district.name =~ '.*t.*' or str(district.id) =~ '.*t.*' or district.uuid =~ '.*t.*'\n" +
                " optional match (district)<--(region:Region)\n" +
                "where region.name =~ '.*t.*' or str(region.id) =~ '.*t.*' or region.uuid =~ '.*t.*'\n" +
                "return customer"
    }
}
