package com.omnitech.chai.util

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.District
import com.omnitech.chai.model.Region
import com.omnitech.chai.model.SubCounty
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import spock.lang.Specification

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by kay on 10/7/14.
 */
class CypherGeneratorTest extends Specification {

    void setup() {
        CypherGenImpl.inTests = true
    }

    def 'test get Assoc arrow'() {

        def field = ReflectFunctions.findAllFields(Customer).find { it.type == SubCounty }

        when:
        def arrow = CypherGenImpl.getAssocArrow(field)

        then:
        arrow == '-[:CUST_IN_SC]->'

        when:
        field = ReflectFunctions.findAllFields(District).find { it.type == Region }
        arrow = CypherGenImpl.getAssocArrow(field)

        then:
        arrow == '<-[:HAS_DISTRICT]-'
    }

    def 'test generation 1'() {
        when:
        def query = CypherGenerator.getPaginatedQuery(BarEntity, [:]).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                ' optional match (barentity)-[:]->(BarEntity_fooBarEntity:FooBarEntity)\n' +
                ' optional match (BarEntity_fooBarEntity)<-[:HAS_CHILD]-(FooBarEntity_parent:FooBarEntity)\n' +
                'WITH barentity,BarEntity_fooBarEntity,FooBarEntity_parent\n' +
                'WHERE barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search} or BarEntity_fooBarEntity.description =~ {search} or str(BarEntity_fooBarEntity.id) =~ {search} or BarEntity_fooBarEntity.uuid =~ {search} or FooBarEntity_parent.description =~ {search} or str(FooBarEntity_parent.id) =~ {search} or FooBarEntity_parent.uuid =~ {search}\n' +
                'return barentity\n' +
                'order by barentity.id desc\n' +
                'skip 0\n' +
                'limit 50\n' +
                ''
    }

    def 'test generation 2'() {
        when:
        def query = CypherGenerator.getPaginatedQuery(BarEntity, [max: 20, offset: 25]).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                ' optional match (barentity)-[:]->(BarEntity_fooBarEntity:FooBarEntity)\n' +
                ' optional match (BarEntity_fooBarEntity)<-[:HAS_CHILD]-(FooBarEntity_parent:FooBarEntity)\n' +
                'WITH barentity,BarEntity_fooBarEntity,FooBarEntity_parent\n' +
                'WHERE barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search} or BarEntity_fooBarEntity.description =~ {search} or str(BarEntity_fooBarEntity.id) =~ {search} or BarEntity_fooBarEntity.uuid =~ {search} or FooBarEntity_parent.description =~ {search} or str(FooBarEntity_parent.id) =~ {search} or FooBarEntity_parent.uuid =~ {search}\n' +
                'return barentity\n' +
                'order by barentity.id desc\n' +
                'skip 20\n' +
                'limit 20\n' +
                ''
    }

    def 'test generation 3'() {
        when:
        def query = CypherGenerator.getCountQuery(BarEntity).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                ' optional match (barentity)-[:]->(BarEntity_fooBarEntity:FooBarEntity)\n' +
                ' optional match (BarEntity_fooBarEntity)<-[:HAS_CHILD]-(FooBarEntity_parent:FooBarEntity)\n' +
                'WITH barentity,BarEntity_fooBarEntity,FooBarEntity_parent\n' +
                'WHERE barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search} or BarEntity_fooBarEntity.description =~ {search} or str(BarEntity_fooBarEntity.id) =~ {search} or BarEntity_fooBarEntity.uuid =~ {search} or FooBarEntity_parent.description =~ {search} or str(FooBarEntity_parent.id) =~ {search} or FooBarEntity_parent.uuid =~ {search}\n' +
                'return count(barentity)\n' +
                ''
    }

    def 'test generation 4'() {
        when:
        def query = CypherGenerator.getCountQuery(FooBarEntity).toString()

        then:
        query == 'MATCH (foobarentity:FooBarEntity)\n' +
                ' optional match (foobarentity)<-[:HAS_CHILD]-(FooBarEntity_parent:FooBarEntity)\n' +
                'WITH foobarentity,FooBarEntity_parent\n' +
                'WHERE foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search} or FooBarEntity_parent.description =~ {search} or str(FooBarEntity_parent.id) =~ {search} or FooBarEntity_parent.uuid =~ {search}\n' +
                'return count(foobarentity)\n' +
                ''

    }

    def 'test generation 5'() {
        when:
        def query = CypherGenerator.getCountQuery(FooBarEntity).toString()

        then:
        query == 'MATCH (foobarentity:FooBarEntity)\n' +
                ' optional match (foobarentity)<-[:HAS_CHILD]-(FooBarEntity_parent:FooBarEntity)\n' +
                'WITH foobarentity,FooBarEntity_parent\n' +
                'WHERE foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search} or FooBarEntity_parent.description =~ {search} or str(FooBarEntity_parent.id) =~ {search} or FooBarEntity_parent.uuid =~ {search}\n' +
                'return count(foobarentity)\n' +
                ''
    }

    def 'test generation wit sort ascending'() {
        when:
        def query = CypherGenerator.getPaginatedQuery(BarEntity, [max: 20, offset: 25, sort: 'name']).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                ' optional match (barentity)-[:]->(BarEntity_fooBarEntity:FooBarEntity)\n' +
                ' optional match (BarEntity_fooBarEntity)<-[:HAS_CHILD]-(FooBarEntity_parent:FooBarEntity)\n' +
                'WITH barentity,BarEntity_fooBarEntity,FooBarEntity_parent\n' +
                'WHERE barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search} or BarEntity_fooBarEntity.description =~ {search} or str(BarEntity_fooBarEntity.id) =~ {search} or BarEntity_fooBarEntity.uuid =~ {search} or FooBarEntity_parent.description =~ {search} or str(FooBarEntity_parent.id) =~ {search} or FooBarEntity_parent.uuid =~ {search}\n' +
                'return barentity\n' +
                'order by barentity.name ASC\n' +
                'skip 20\n' +
                'limit 20\n'
    }


    def 'test generation wit sort descding'() {
        when:
        def query = CypherGenerator.getPaginatedQuery(BarEntity, [max: 20, offset: 25, sort: 'name', order: 'desc']).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                ' optional match (barentity)-[:]->(BarEntity_fooBarEntity:FooBarEntity)\n' +
                ' optional match (BarEntity_fooBarEntity)<-[:HAS_CHILD]-(FooBarEntity_parent:FooBarEntity)\n' +
                'WITH barentity,BarEntity_fooBarEntity,FooBarEntity_parent\n' +
                'WHERE barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search} or BarEntity_fooBarEntity.description =~ {search} or str(BarEntity_fooBarEntity.id) =~ {search} or BarEntity_fooBarEntity.uuid =~ {search} or FooBarEntity_parent.description =~ {search} or str(FooBarEntity_parent.id) =~ {search} or FooBarEntity_parent.uuid =~ {search}\n' +
                'return barentity\n' +
                'order by barentity.name DESC\n' +
                'skip 20\n' +
                'limit 20\n'
    }

    def 'test generation with levels'() {
        when:
        def query = CypherGenerator.getNonPaginatedQuery(MCustomer, 2,Collections.EMPTY_MAP).toString()

        then:
        query == 'MATCH (mcustomer:MCustomer)\n' +
                ' optional match (mcustomer)-[:VILL]->(MCustomer_village:MVillage)\n' +
                ' optional match (MCustomer_village)-[:PAR]->(MVillage_parish:MParish)\n' +
                'WITH mcustomer,MCustomer_village,MVillage_parish\n' +
                'WHERE mcustomer.name =~ {search} or mcustomer.address =~ {search} or MCustomer_village.name =~ {search} or MVillage_parish.name =~ {search}\n' +
                'return mcustomer\n'

    }

    def 'test generation with levels 0'() {
        when:
        def query = CypherGenerator.getNonPaginatedQuery(MCustomer, 0,Collections.EMPTY_MAP).toString()

        then:
        query == 'MATCH (mcustomer:MCustomer)\n' +
                'WITH mcustomer\n' +
                'WHERE mcustomer.name =~ {search} or mcustomer.address =~ {search}\n' +
                'return mcustomer\n'

    }

    def 'test generation with levels 1'() {
        when:
        def query = CypherGenerator.getNonPaginatedQuery(MCustomer, 1, [:]).toString()

        then:
        query == 'MATCH (mcustomer:MCustomer)\n' +
                ' optional match (mcustomer)-[:VILL]->(MCustomer_village:MVillage)\n' +
                'WITH mcustomer,MCustomer_village\n' +
                'WHERE mcustomer.name =~ {search} or mcustomer.address =~ {search} or MCustomer_village.name =~ {search}\n' +
                'return mcustomer\n'

    }

    def 'test generation with high level'() {
        when:
        def query = CypherGenerator.getNonPaginatedQuery(MCustomer, 10, [:]).toString()

        then:
        query == 'MATCH (mcustomer:MCustomer)\n' +
                ' optional match (mcustomer)-[:VILL]->(MCustomer_village:MVillage)\n' +
                ' optional match (MCustomer_village)-[:PAR]->(MVillage_parish:MParish)\n' +
                ' optional match (MVillage_parish)-[:SUB]->(MParish_subCounty:MSubCounty)\n' +
                ' optional match (MParish_subCounty)-[:DIS]->(MSubCounty_district:MDistrict)\n' +
                'WITH mcustomer,MCustomer_village,MVillage_parish,MParish_subCounty,MSubCounty_district\n' +
                'WHERE mcustomer.name =~ {search} or mcustomer.address =~ {search} or MCustomer_village.name =~ {search} or MVillage_parish.name =~ {search} or MParish_subCounty.name =~ {search} or MSubCounty_district.name =~ {search}\n' +
                'return mcustomer\n'
    }

    def 'test generation with fielter'() {
        when:
        def filters = [
                allow: [
                        [class: MCustomer.simpleName],
                        [class: MVillage.simpleName, patterns: ['parish']],
                        [class: MParish.simpleName, patterns: ['subCounty']],
                        [class: MSubCounty.simpleName, patterns: ['name']],
                ]
        ]
        def query = CypherGenerator.getNonPaginatedQuery(MCustomer, 10, filters).toString()

        then:
        query == 'MATCH (mcustomer:MCustomer)\n' +
                ' optional match (mcustomer)-[:VILL]->(MCustomer_village:MVillage)\n' +
                ' optional match (MCustomer_village)-[:PAR]->(MVillage_parish:MParish)\n' +
                ' optional match (MVillage_parish)-[:SUB]->(MParish_subCounty:MSubCounty)\n' +
                'WITH mcustomer,MCustomer_village,MVillage_parish,MParish_subCounty\n' +
                'WHERE mcustomer.name =~ {search} or mcustomer.address =~ {search} or MParish_subCounty.name =~ {search}\n' +
                'return mcustomer\n'
    }


}


class AbstractEntity {
    private static DateFormat format = new SimpleDateFormat('yyyy-MM-dd hh:mm:ss')
    @GraphId
    Long id
    String uuid
    Date dateCreated
    Date lastUpdated
}


@NodeEntity
class FooBarEntity extends AbstractEntity {
    @RelatedTo
    List<BarEntity> barEntities
    @RelatedTo(type = 'HAS_CHILD', direction = Direction.INCOMING)
    FooBarEntity parent
    String description

}

@NodeEntity
class BarEntity extends AbstractEntity {

    @RelatedTo
    FooBarEntity fooBarEntity

    String name
}

@NodeEntity
class Bar {
    String description

    BarEntity foo
    transient String transientName3

    String getFooBar() { "" }
}

@NodeEntity
class MDistrict {
    String name
}

@NodeEntity
class MSubCounty {
    String name
    @RelatedTo(type = 'DIS')
    MDistrict district
}

@NodeEntity
class MParish {
    String name
    @RelatedTo(type = 'SUB')
    MSubCounty subCounty
}

@NodeEntity
class MVillage {
    String name
    @RelatedTo(type = 'PAR')
    MParish parish
}

@NodeEntity
class MCustomer {
    String name
    String address
    @RelatedTo(type = 'VILL')
    MVillage village
}

