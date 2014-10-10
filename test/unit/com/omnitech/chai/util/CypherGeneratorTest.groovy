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
        arrow == '-->'

        when:
        field = ReflectFunctions.findAllFields(District).find { it.type == Region }
        arrow = ModelFunctions.getAssocArrow(field)

        then:
        arrow == '<--'
    }

    def 'test generation'() {
        when:
        def query = CypherGenerator.getPaginatedQuery(BarEntity, [:]).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                'where barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search}\n' +
                ' optional match (barentity)-->(foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                ' optional match (foobarentity)<--(foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                'return barentity\n' +
                'order by barentity.id desc\n' +
                'skip 0\n' +
                'limit 50\n'

        when:
        query = CypherGenerator.getPaginatedQuery(BarEntity, [max: 20, offset: 25]).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                'where barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search}\n' +
                ' optional match (barentity)-->(foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                ' optional match (foobarentity)<--(foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                'return barentity\n' +
                'order by barentity.id desc\n' +
                'skip 20\n' +
                'limit 20\n'


        when:
        query = CypherGenerator.getCountQuery(BarEntity).toString()

        then:
        query == 'MATCH (barentity:BarEntity)\n' +
                'where barentity.name =~ {search} or str(barentity.id) =~ {search} or barentity.uuid =~ {search}\n' +
                ' optional match (barentity)-->(foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                ' optional match (foobarentity)<--(foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                'return count(barentity)\n'

        when:
        query = CypherGenerator.getCountQuery(FooBarEntity).toString()

        then:
        query == 'MATCH (foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                ' optional match (foobarentity)<--(foobarentity:FooBarEntity)\n' +
                'where foobarentity.description =~ {search} or str(foobarentity.id) =~ {search} or foobarentity.uuid =~ {search}\n' +
                'return count(foobarentity)\n'
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
