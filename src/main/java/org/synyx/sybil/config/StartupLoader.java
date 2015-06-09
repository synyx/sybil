package org.synyx.sybil.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.synyx.sybil.brick.BrickConfigLoader;
import org.synyx.sybil.bricklet.BrickletNameRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripConfigLoader;
import org.synyx.sybil.bricklet.output.relay.RelayConfigLoader;

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

    private BrickletNameRegistry brickletNameRegistry;

    private LEDStripConfigLoader ledStripConfigLoader;

    private RelayConfigLoader relayConfigLoader;

    @Autowired
    public StartupLoader(ConfigLoader configLoader, BrickConfigLoader brickConfigLoader,
        BrickletNameRegistry brickletNameRegistry, LEDStripConfigLoader ledStripConfigLoader,
        RelayConfigLoader relayConfigLoader) {

        this.configLoader = configLoader;
        this.brickConfigLoader = brickConfigLoader;
        this.brickletNameRegistry = brickletNameRegistry;
        this.ledStripConfigLoader = ledStripConfigLoader;
        this.relayConfigLoader = relayConfigLoader;
    }

    @PostConstruct
    public void init() {

        LOG.info("Loading Startup Configuration");

        brickletNameRegistry.clear();

        brickConfigLoader.loadBricksConfig();

        brickConfigLoader.resetBricks();

        ledStripConfigLoader.loadLEDStripConfig();

        relayConfigLoader.loadRelayConfig();

        configLoader.loadConfig();
    }
}
