package com.omnitech.chai.crm

import com.omnitech.chai.model.District
import com.omnitech.chai.model.SubCounty
import com.omnitech.chai.model.Territory
import com.omnitech.chai.repositories.SubCountyRepository
import com.omnitech.chai.repositories.TerritoryRepository
import org.springframework.data.neo4j.support.Neo4jTemplate
import spock.lang.Specification

/**
 * Created by kay on 2/19/2015.
 */


class RegionServiceTest extends Specification {

    District district
    District district2
    Territory territory
    RegionService service = new RegionService()

    def setup() {
        district = new District([name       : 'Kkaka',
                                 id         : 1,
                                 subCounties: [
                                         new SubCounty(name: '22', id: 22),
                                         new SubCounty(name: '24', id: 24),
                                         new SubCounty(name: '25', id: 25)]
        ])
        district.subCounties.each { it.district = district }


        district2 = new District([name       : 'D2',
                                  id         : 2,
                                  subCounties: [
                                          new SubCounty(name: '30', id: 30),
                                          new SubCounty(name: '31', id: 31),
                                          new SubCounty(name: '32', id: 32)]
        ])
        district2.subCounties.each { it.district = district2 }

        territory = new Territory(id: 4, name: 'Territory33', subCounties: district.subCounties)

    }

    def "test deleting mappings"() {
        service.territoryRepository = Mock(TerritoryRepository)
        service.neo = Mock(Neo4jTemplate)

        when:
        service.mapTerritoryToSubs(territory.id, district.id, [])

        then:
        1 * service.territoryRepository.findOne(territory.id) >> territory
        1 * service.territoryRepository.save(territory)
        territory.subCounties.size() == 0
    }

    def "test deleting some subs and leave others in a district"() {
        service.territoryRepository = Mock(TerritoryRepository)
        service.neo = Mock(Neo4jTemplate)
        service.subCountyRepository = Mock(SubCountyRepository)

        when:
        service.mapTerritoryToSubs(territory.id, district.id, [25])

        then:
        1 * service.territoryRepository.findOne(territory.id) >> territory
        1 * service.territoryRepository.save(territory)
        1 * service.subCountyRepository.findOne(25) >> district.subCounties.find {it.id == 25}
        territory.subCounties.size() == 1
    }

    def "test test unmapping on whole district"() {
        service.territoryRepository = Mock(TerritoryRepository)
        service.neo = Mock(Neo4jTemplate)
        service.subCountyRepository = Mock(SubCountyRepository)

        when:
        territory.subCounties.addAll(district2.subCounties)
        service.mapTerritoryToSubs(territory.id, district.id, [])

        then:
        1 * service.territoryRepository.findOne(territory.id) >> territory
        1 * service.territoryRepository.save(territory)
        territory.subCounties.size() == 3
    }


}
