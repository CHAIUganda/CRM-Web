package com.omnitech.chai.crm

import com.omnitech.chai.model.Setting
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

@Neo4jTransactional
class SettingService {

    def settingRepository
    @Autowired
    Neo4jTemplate neo

    /* Settings */

    List<Setting> listAllSettings() { settingRepository.findAll().collect() }

    Page<Setting> listSettings(Map params) { ModelFunctions.listAll(settingRepository, params) }

    Setting findSetting(Long id) { settingRepository.findOne(id) }

    Setting saveSetting(Setting setting) { ModelFunctions.saveEntity(settingRepository, setting) }

    void deleteSetting(Long id) { settingRepository.delete(id) }

    Page<Setting> searchSettings(String search, Map params) {
        ModelFunctions.searchAll(neo, Setting, ModelFunctions.getWildCardRegex(search), params)
    }
}