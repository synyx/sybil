package org.synyx.sybil.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.annotation.PostConstruct;


/**
 * StartUpLoader. Loads initial configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class StartupLoader {

    private static final Logger LOG = LoggerFactory.getLogger(StartupLoader.class);

    private JSONConfigLoader jsonConfigLoader;

    @Autowired
    public StartupLoader(JSONConfigLoader jsonConfigLoader) {

        this.jsonConfigLoader = jsonConfigLoader;
    }

    @PostConstruct
    public void init() throws IOException {

        LOG.info("Loading Startup Configuration");

        jsonConfigLoader.loadConfig();
    }
}
