package com.omnitech.chai.util

import com.omnitech.chai.model.*
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
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
        arrow == '-[:BELONGS_TO_SC]->'

        when:
        field = ReflectFunctions.findAllFields(District).find { it.type == Region }
        arrow = CypherGenerator.getAssocArrow(field)

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
        def query = CypherGenerator.getPaginatedQuery(BarEntity, [max: 20, offset: 25,sort:'name']).toString()

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
        def query = CypherGenerator.getPaginatedQuery(BarEntity, [max: 20, offset: 25,sort:'name',order:'desc']).toString()

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

