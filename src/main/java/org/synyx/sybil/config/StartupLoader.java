package org.synyx.sybil.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Profile;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * StartUpLoader. Loads initial configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Profile("default")
@Component
public class StartupLoader {

    private static final Logger LOG = LoggerFactory.getLogger(StartupLoader.class);

    private ConfigLoader configLoader;

    @Autowired
    public StartupLoader(ConfigLoader configLoader) {

        this.configLoader = configLoader;
    }

    @PostConstruct
    public void init() {

        LOG.info("Loading Startup Configuration");

        configLoader.loadConfig();
    }
}
