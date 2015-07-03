package org.synyx.sybil.jenkins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripCustomColors;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.SingleStatusOnLEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.jenkins.config.JenkinsConfig;
import org.synyx.sybil.jenkins.domain.Status;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;


/**
 * JenkinsConfigLoader.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class JenkinsConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsConfigLoader.class);
    private final ObjectMapper mapper;
    private final String configDirectory;
    private final String jenkinsServerConfigFile;
    private final LEDStripService ledStripService;
    private final SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry;
    private final JenkinsConfig jenkinsConfig;
    private final LEDStripCustomColors ledStripCustomColors;

    /**
     * Instantiates a new Jenkins config loader.
     *
     * @param  environment  The Environment (provided by Spring, contains the configuration read from config.properties)
     * @param  ledStripService  the LEDStrip registry
     * @param  jenkinsConfig  the jenkins configuration
     * @param  singleStatusOnLEDStripRegistry  the SingleStatusOnLEDStrip registry
     * @param  ledStripCustomColors  the led strip custom colors
     * @param  objectMapper  the mapper
     */
    @Autowired
    public JenkinsConfigLoader(Environment environment, LEDStripService ledStripService, JenkinsConfig jenkinsConfig,
        SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry, LEDStripCustomColors ledStripCustomColors,
        ObjectMapper objectMapper) {

        configDirectory = environment.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = environment.getProperty("jenkins.configfile");
        this.ledStripService = ledStripService;
        this.jenkinsConfig = jenkinsConfig;
        this.singleStatusOnLEDStripRegistry = singleStatusOnLEDStripRegistry;
        this.ledStripCustomColors = ledStripCustomColors;
        this.mapper = objectMapper;
    }

    /**
     * Load jenkins servers.
     */
    public void loadJenkinsServers() {

        LOG.info("Loading Jenkins servers");

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                List<Map<String, Object>> servers = mapper.readValue(new File(jenkinsServerConfigFile),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                for (Map server : servers) {
                    jenkinsConfig.putServer(server.get("url").toString(), server.get("user").toString(),
                        server.get("key").toString());
                }
            } catch (IOException e) {
                LOG.error("Error loading jenkinsservers.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadJenkinsServers");
            }
        }
    }


    /**
     * Load jenkins config.
     */
    public void loadJenkinsConfig() {

        loadJenkinsConfig("jenkins.json");
    }


    /**
     * Load jenkins config.
     *
     * @param  file  the file
     */
    public void loadJenkinsConfig(String file) {

        LOG.info("Loading Jenkins configuration");

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                Map<String, List<Map<String, Object>>> jenkinsConfigData = mapper.readValue(new File(
                            configDirectory + file), new TypeReference<Map<String, List<Map<String, Object>>>>() {
                        }); // fetch Jenkins configuration data...

                jenkinsConfig.reset();

                HealthController.setHealth(Status.OKAY, "loadJenkinsConfig");

                // ... deserialize the data manually
                for (String server : jenkinsConfigData.keySet()) { // iterate over all the servers

                    for (Map line : jenkinsConfigData.get(server)) { // get each configuration line for each server

                        String jobName = line.get("name").toString();
                        String ledstrip = line.get("ledstrip").toString();

                        LEDStripDomain ledStripDomain = ledStripService.getDomain(ledstrip.toLowerCase());

                        LEDStrip ledStrip = ledStripService.getLEDStrip(ledStripDomain);

                        if (ledStrip == null) {
                            LOG.warn("Ledstrip {} does not exist.", ledstrip);

                            if (HealthController.getHealth() != Status.CRITICAL) {
                                HealthController.setHealth(Status.WARNING, "loadJenkinsConfig");
                            }
                        } else {
                            Map<String, Color> colors = ledStripCustomColors.get(ledstrip);

                            if (colors == null) {
                                jenkinsConfig.put(server, jobName, singleStatusOnLEDStripRegistry.get(ledStrip));
                            } else {
                                jenkinsConfig.put(server, jobName,
                                    singleStatusOnLEDStripRegistry.get(ledStrip, colors.get("okay"),
                                        colors.get("warning"), colors.get("critical")));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Error loading jenkins.json: {}", e.toString());
                HealthController.setHealth(Status.WARNING, "loadJenkinsConfig");
            }
        }
    }
}
