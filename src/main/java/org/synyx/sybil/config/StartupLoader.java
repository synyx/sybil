package org.synyx.sybil.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.synyx.sybil.brick.BrickConfigLoader;
import org.synyx.sybil.bricklet.BrickletNameService;
import org.synyx.sybil.bricklet.input.SensorConfigLoader;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripConfigLoader;
import org.synyx.sybil.jenkins.JenkinsConfigLoader;

import javax.annotation.PostConstruct;


/**
 * StartUpLoader. Loads initial configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class StartupLoader {

    private static final Logger LOG = LoggerFactory.getLogger(StartupLoader.class);
    private final JenkinsConfigLoader jenkinsConfigLoader;
    private final BrickConfigLoader brickConfigLoader;
    private final BrickletNameService brickletNameRegistry;
    private final LEDStripConfigLoader ledStripConfigLoader;
    private final SensorConfigLoader sensorConfigLoader;

    @Autowired
    public StartupLoader(JenkinsConfigLoader jenkinsConfigLoader, BrickConfigLoader brickConfigLoader,
        BrickletNameService brickletNameRegistry, LEDStripConfigLoader ledStripConfigLoader,
        SensorConfigLoader sensorConfigLoader) {

        this.jenkinsConfigLoader = jenkinsConfigLoader;
        this.brickConfigLoader = brickConfigLoader;
        this.brickletNameRegistry = brickletNameRegistry;
        this.ledStripConfigLoader = ledStripConfigLoader;
        this.sensorConfigLoader = sensorConfigLoader;
    }

    @PostConstruct
    public void init() {

        LOG.info("Loading Startup Configuration");

        brickletNameRegistry.clear();

        brickConfigLoader.loadBricksConfig();

        brickConfigLoader.resetAllBricks();

        ledStripConfigLoader.loadLEDStripConfig();

        sensorConfigLoader.loadSensorConfig();

        jenkinsConfigLoader.loadJenkinsServers();

        jenkinsConfigLoader.loadJenkinsConfig();
    }
}
