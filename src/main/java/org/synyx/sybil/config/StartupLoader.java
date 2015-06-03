package org.synyx.sybil.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.synyx.sybil.brick.BrickConfigLoader;

import javax.annotation.PostConstruct;


/**
 * StartUpLoader. Loads initial configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class StartupLoader {

    private static final Logger LOG = LoggerFactory.getLogger(StartupLoader.class);

    private ConfigLoader configLoader;

    private BrickConfigLoader brickConfigLoader;

    @Autowired
    public StartupLoader(ConfigLoader configLoader, BrickConfigLoader brickConfigLoader) {

        this.configLoader = configLoader;
        this.brickConfigLoader = brickConfigLoader;
    }

    @PostConstruct
    public void init() {

        LOG.info("Loading Startup Configuration");

        brickConfigLoader.loadBricksConfig();

        brickConfigLoader.resetBricks();

        configLoader.loadConfig();
    }
}
