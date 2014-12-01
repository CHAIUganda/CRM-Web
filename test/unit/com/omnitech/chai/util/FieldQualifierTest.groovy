package com.omnitech.chai.util

import com.omnitech.chai.model.Customer
import spock.lang.Specification

/**
 * Created by kay on 12/1/14.
 */
class FieldQualifierTest extends Specification {

    def "IsAllowed"() {
        when:
        def pattern = [
                allow: [
                        ['class': 'Custo*', patterns: ['*Name', 'b*', 'field', 'deniedCustomerField']],
                        ['class': 'DeniedClass*', patterns: ['*Name', 'b*', 'field', 'deniedCustomerField']],
                        ['class': 'AllAllowedClass']
                ],
                deny: [
                        ['class': 'Customer', patterns: ['deniedCustomerField']],
                        ['class': 'DeniedClass'],
                        ['class': 'PartialDeniedClass', patterns: ['deniedField']]
                ]

        ]

        then:
        //Testing fields on Customers
        FieldQualifier.isAllowed(pattern, Customer, 'firstName')
        FieldQualifier.isAllowed(pattern, Customer, 'boyName')
        FieldQualifier.isAllowed(pattern, Customer, 'field')

        //Test when whole class is allowed
        FieldQualifier.isAllowed(pattern, 'AllAllowedClass', 'field')

        //test when field is not in allowed
        !FieldQualifier.isAllowed(pattern, Customer, 'someIllegalField')

        //Testing Partial Denied Fields
        !FieldQualifier.isAllowed(pattern, Customer, 'deniedCustomerField')

        //Test when a whole class is denied
        !FieldQualifier.isAllowed(pattern, 'DeniedClass', 'firstName')

        //Test when a few fields are denied and the rest allowed
        !FieldQualifier.isAllowed(pattern, 'PartialDeniedClass', 'deniedField')
        //this should also be false since the allow field is not empty
        !FieldQualifier.isAllowed(pattern, 'PartialDeniedClass', 'anotherField')


    }

    def "Test With only Allowed"() {
        when:
        def pattern = [
                allow: [
                        ['class': 'Custo*', patterns: ['*Name', 'b*', 'field', 'deniedCustomerField']],
                        ['class': 'DeniedClass*', patterns: ['*Name', 'b*', 'field', 'deniedCustomerField']],
                        ['class': 'AllAllowedClass']
                ]
        ]

        then:
        //Testing fields on Customers
        FieldQualifier.isAllowed(pattern, Customer, 'firstName')
        FieldQualifier.isAllowed(pattern, Customer, 'boyName')
        FieldQualifier.isAllowed(pattern, Customer, 'field')
        FieldQualifier.isAllowed(pattern, Customer, 'deniedCustomerField')

        //Test when whole class is allowed
        FieldQualifier.isAllowed(pattern, 'AllAllowedClass', 'field')

        //Test another entry
        FieldQualifier.isAllowed(pattern, 'DeniedClass', 'firstName')

        //test when field is not in allowed
        !FieldQualifier.isAllowed(pattern, Customer, 'someIllegalField')

        //When a field does not exist in Allowed
        !FieldQualifier.isAllowed(pattern, 'RandomClass', 'deniedField')
    }

    def "Test with denied only"() {
        when:
        def pattern = [
                deny: [
                        ['class': 'Customer', patterns: ['deniedCustomerField']],
                        ['class': 'DeniedClass'],
                        ['class': 'PartialDeniedClass', patterns: ['deniedField']]
                ]

        ]

        then:
        //Testing not in denied
        FieldQualifier.isAllowed(pattern, Customer, 'firstName')
        FieldQualifier.isAllowed(pattern, 'RandomClass', 'firstName')

        //Testing Partial Denied Fields
        !FieldQualifier.isAllowed(pattern, Customer, 'deniedCustomerField')

        //Test when a whole class is denied
        !FieldQualifier.isAllowed(pattern, 'DeniedClass', 'firstName')

        //Test when a few fields are denied and the rest allowed
        !FieldQualifier.isAllowed(pattern, 'PartialDeniedClass', 'deniedField')
        FieldQualifier.isAllowed(pattern, 'PartialDeniedClass', 'anotherField')


    }

    def "Test with empty Filters"() {
        when:
        def pattern = [:]

        then:
        //Testing not in denied
        FieldQualifier.isAllowed(pattern, Customer, 'firstName')
        FieldQualifier.isAllowed(pattern, 'RandomClass', 'firstName')
    }
}
