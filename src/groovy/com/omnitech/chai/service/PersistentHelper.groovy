package com.omnitech.chai.service

import com.omnitech.chai.model.AbstractEntity
import com.omnitech.chai.util.ChaiUtils
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.SmartApplicationListener
import org.springframework.data.neo4j.lifecycle.BeforeSaveEvent

/**
 * Created by kay on 9/28/14.
 */
class PersistentHelper implements SmartApplicationListener {

    @Override
    void onApplicationEvent(ApplicationEvent event) {
        BeforeSaveEvent bsEvent = event as BeforeSaveEvent<AbstractEntity>
        def entity = bsEvent?.entity

        if (entity?.hasProperty('uuid')) {
            ChaiUtils.setPropertyIfNull(entity, 'uuid', UUID.randomUUID().toString())
        }

        if (entity?.hasProperty('dateCreated')) {
            ChaiUtils.setPropertyIfNull(entity, 'dateCreated', new Date())
        }

        if (entity?.hasProperty('lastUpdated')) {
            entity.setProperty('lastUpdated', new Date())
        }
    }


    @Override
    boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == BeforeSaveEvent
    }

    @Override
    boolean supportsSourceType(Class<?> sourceType) {
        return true
    }

    @Override
    int getOrder() {
        return 0
    }

}
