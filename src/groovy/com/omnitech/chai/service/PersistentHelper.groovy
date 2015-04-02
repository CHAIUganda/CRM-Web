package com.omnitech.chai.service

import com.omnitech.chai.model.AbstractEntity
import com.omnitech.chai.util.ModelFunctions
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.SmartApplicationListener
import org.springframework.data.neo4j.lifecycle.BeforeDeleteEvent
import org.springframework.data.neo4j.lifecycle.BeforeSaveEvent

/**
 * Created by kay on 9/28/14.
 */
class PersistentHelper implements SmartApplicationListener {

    @Override
    void onApplicationEvent(ApplicationEvent event) {

        switch (event.getClass()) {
            case BeforeSaveEvent:
                beforeSave(event as BeforeSaveEvent<AbstractEntity>)
                break
            case BeforeDeleteEvent:
                beforeDelete(event as BeforeDeleteEvent)
                break
        }


    }

    private static beforeSave(BeforeSaveEvent<AbstractEntity> bsEvent) {
        def entity = bsEvent?.entity

        if (entity.id) {
            ModelFunctions.setPropertyIfNull(entity, 'uuid', UUID.randomUUID().toString())
        } else {
            ModelFunctions.setProperty(entity, 'uuid', UUID.randomUUID().toString())
        }
        ModelFunctions.setPropertyIfNull(entity, 'dateCreated', new Date())
        ModelFunctions.setProperty(entity, 'lastUpdated', new Date())

        if (entity.respondsTo('beforeSave')) entity.'beforeSave'()

    }

    private static beforeDelete(BeforeDeleteEvent<AbstractEntity> bsEvent) {
        def entity = bsEvent?.entity
        if (entity.respondsTo('beforeDelete')) entity.'beforeDelete'()
    }

    @Override
    boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == BeforeSaveEvent || eventType == BeforeDeleteEvent
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
