package com.omnitech.mis.utils;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * User: victor
 * Date: 8/7/14
 */
public class MyIdGenerator implements IdentifierGenerator {
    public Serializable generate(SessionImplementor session, Object object) {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}