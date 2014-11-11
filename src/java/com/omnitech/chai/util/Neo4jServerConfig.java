package com.omnitech.chai.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ThirdPartyJaxRsPackage;

import java.util.*;

public class Neo4jServerConfig implements  Configurator {

    private Configuration config;

    public Neo4jServerConfig(Map<String, String> config) {
        this.config = new MapConfiguration(config);
    }

    @Override
    public Configuration configuration() {
        return config; 
    }

    @Override
    public Map<String, String> getDatabaseTuningProperties() {
        return new HashMap<String, String>();
    }

    @Override
    public List<ThirdPartyJaxRsPackage> getThirdpartyJaxRsClasses() {
        return new ArrayList<ThirdPartyJaxRsPackage>();
    }

    @Override
    public List<ThirdPartyJaxRsPackage> getThirdpartyJaxRsPackages() {
        return new ArrayList<ThirdPartyJaxRsPackage>();
    }
}