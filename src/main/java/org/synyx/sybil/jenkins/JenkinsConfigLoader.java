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
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
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

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsConfigLoader.class);

    // Jackson ObjectMapper, maps JSON to Java Objects
    private ObjectMapper mapper;

    // The place where the config files lie, taken from the injected environment (and thus ultimately a properties file)
    private String configDir;

    // The file where the Jenkins servers are configured
    private String jenkinsServerConfigFile;

    // The Repository to save LEDStrip configuration data
    private LEDStripRepository LEDStripRepository;

    // This fetches the actual LED Strip objects for given config data
    private LEDStripService LEDStripService;

    // Fetches one SingleStatusOnLEDStrip for each LED Strip
    private SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry;

    // The object that saves the Jenkins servers and job configurations
    private JenkinsConfig jenkinsConfig;

    // Map saving the custom status colors for SingleStatusOnLEDStrips
    private LEDStripCustomColors ledStripCustomColors;

    /**
     * Instantiates a new Jenkins config loader.
     *
     * @param  LEDStripRepository  The LEDStrip repository
     * @param  environment  The Environment (provided by Spring, contains the configuration read from config.properties)
     * @param  LEDStripService  the LEDStrip registry
     * @param  jenkinsConfig  the jenkins configuration
     * @param  singleStatusOnLEDStripRegistry  the SingleStatusOnLEDStrip registry
     * @param  ledStripCustomColors  the led strip custom colors
     * @param  objectMapper  the mapper
     */
    @Autowired
    public JenkinsConfigLoader(LEDStripRepository LEDStripRepository, Environment environment,
        LEDStripService LEDStripService, JenkinsConfig jenkinsConfig,
        SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry, LEDStripCustomColors ledStripCustomColors,
        ObjectMapper objectMapper) {

        this.LEDStripRepository = LEDStripRepository;
        configDir = environment.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = environment.getProperty("jenkins.configfile");
        this.LEDStripService = LEDStripService;
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
                Map<String, List<Map<String, Object>>> jenkinsConfigData = mapper.readValue(new File(configDir + file),
                        new TypeReference<Map<String, List<Map<String, Object>>>>() {
                        }); // fetch Jenkins configuration data...

                jenkinsConfig.reset();

                HealthController.setHealth(Status.OKAY, "loadJenkinsConfig");

                // ... deserialize the data manually
                for (String server : jenkinsConfigData.keySet()) { // iterate over all the servers

                    for (Map line : jenkinsConfigData.get(server)) { // get each configuration line for each server

                        String jobName = line.get("name").toString();
                        String ledstrip = line.get("ledstrip").toString();

                        LEDStripDomain LEDStripDomain = LEDStripRepository.findByName(ledstrip.toLowerCase()); // names are always lowercase

                        LEDStrip LEDStrip = LEDStripService.getLEDStrip(LEDStripDomain);

                        if (LEDStrip != null) {
                            Map<String, Color> colors = ledStripCustomColors.get(ledstrip);

                            if (colors != null) {
                                jenkinsConfig.put(server, jobName,
                                    singleStatusOnLEDStripRegistry.get(LEDStrip, colors.get("okay"),
                                        colors.get("warning"), colors.get("critical")));
                            } else {
                                jenkinsConfig.put(server, jobName, singleStatusOnLEDStripRegistry.get(LEDStrip));
                            }
                        } else {
                            LOG.warn("Ledstrip {} does not exist.", ledstrip);

                            if (HealthController.getHealth() != Status.CRITICAL) {
                                HealthController.setHealth(Status.WARNING, "loadJenkinsConfig");
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
