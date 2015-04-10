package org.synyx.sybil.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.common.jenkins.JenkinsConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.in.Status;
import org.synyx.sybil.out.Color;
import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.OutputLEDStripRegistry;
import org.synyx.sybil.out.SingleStatusOnLEDStripRegistry;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JSONConfigLoader. Loads complete or partial configurations from JSON files.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class ConfigLoader {

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

    // Jackson ObjectMapper, maps JSON to Java Objects
    private ObjectMapper mapper = new ObjectMapper();

    // The place where the config files lie, taken from the injected environment (and thus ultimately a properties file)
    private String configDir;

    // The file where the Jenkins servers are configured
    private String jenkinsServerConfigFile;

    // The Repository to save Brick configuration data
    private BrickRepository brickRepository;

    // The Repository to save OutputLEDStrip configuration data
    private OutputLEDStripRepository outputLEDStripRepository;

    // This fetches the actual LED Strip objects for given config data
    private OutputLEDStripRegistry outputLEDStripRegistry;

    // Fetches one SingleStatusOnLEDStrip for each LED Strip
    private SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry;

    // The object that saves the Jenkins servers and job configurations
    private JenkinsConfig jenkinsConfig;

    // The status of the LED Strip Config
    private Status LEDStripStatus = Status.OKAY;

    // Map saving the custom status colors for SingleStatusOnLEDStrips
    private Map<String, Map<String, Color>> customStatusColors = new HashMap<>();

    /**
     * Instantiates a new JSON config loader.
     *
     * @param  brickRepository  The Brick repository
     * @param  outputLEDStripRepository  The OutputLEDStrip repository
     * @param  env  The Environment (provided by Spring, contains the configuration read from config.properties)
     * @param  outputLEDStripRegistry  the OutputLEDStrip registry
     * @param  jenkinsConfig  the jenkins configuration
     * @param  singleStatusOnLEDStripRegistry  the SingleStatusOnLEDStrip registry
     */
    @Autowired
    public ConfigLoader(BrickRepository brickRepository, OutputLEDStripRepository outputLEDStripRepository,
        Environment env, OutputLEDStripRegistry outputLEDStripRegistry, JenkinsConfig jenkinsConfig,
        SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry) {

        this.brickRepository = brickRepository;
        this.outputLEDStripRepository = outputLEDStripRepository;
        configDir = env.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = env.getProperty("jenkins.configfile");
        this.outputLEDStripRegistry = outputLEDStripRegistry;
        this.jenkinsConfig = jenkinsConfig;
        this.singleStatusOnLEDStripRegistry = singleStatusOnLEDStripRegistry;
    }

    /**
     * Load the complete configuration from JSON files.
     */
    public void loadConfig() {

        try {
            loadBricksConfig();
        } catch (IOException e) {
            LOG.error("Error loading bricks.json: {}", e.toString());
            HealthController.setHealth(Status.CRITICAL);
        }

        try {
            loadLEDStripConfig();
        } catch (IOException e) {
            LOG.error("Error loading ledstrips.json: {}", e.toString());
            HealthController.setHealth(Status.CRITICAL);
        }

        try {
            loadJenkinsServers();
        } catch (IOException e) {
            LOG.error("Error loading jenkinsservers.json: {}", e.toString());
            HealthController.setHealth(Status.CRITICAL);
        }

        try {
            loadJenkinsConfig();
        } catch (IOException e) {
            LOG.error("Error loading jenkins.json: {}", e.toString());
        }
    }


    /**
     * Load bricks config.
     *
     * @throws  IOException  the iO exception
     */
    public void loadBricksConfig() throws IOException {

        LOG.info("Loading Brick configuration");

        List<BrickDomain> bricks = mapper.readValue(new File(configDir + "bricks.json"),
                new TypeReference<List<BrickDomain>>() {
                });

        brickRepository.deleteAll();

        brickRepository.save(bricks); // ... simply dump them into the database
    }


    /**
     * Load lED strip config.
     *
     * @throws  IOException  the iO exception
     */
    public void loadLEDStripConfig() throws IOException {

        LOG.info("Loading LED Strip configuration");

        List<Map<String, Object>> ledstrips = mapper.readValue(new File(configDir + "ledstrips.json"),
                new TypeReference<List<Map<String, Object>>>() {
                });

        outputLEDStripRepository.deleteAll();

        for (Map ledstrip : ledstrips) { // ... deserialize the data manually

            String name = ledstrip.get("name").toString();
            String uid = ledstrip.get("uid").toString();

            try {
                int length = Integer.parseInt(ledstrip.get("length").toString());
                BrickDomain brick = brickRepository.findByName(ledstrip.get("brick").toString()); // fetch the corresponding bricks from the repo

                if (brick != null) { // if there was corresponding brick found in the repo...
                    outputLEDStripRepository.save(new OutputLEDStripDomain(name, uid, length, brick)); // ... save the LED Strip.
                } else { // if not...
                    LOG.error("Brick {} does not exist.", ledstrip.get("brick").toString()); // ... error!
                    HealthController.setHealth(Status.WARNING);
                    LEDStripStatus = Status.WARNING;
                }
            } catch (NumberFormatException e) {
                LOG.error("Failed to load config for LED Strip {}: \"length\" is not an integer.", name);
                HealthController.setHealth(Status.WARNING);
                LEDStripStatus = Status.WARNING;
            }

            if (ledstrip.get("okayRed") != null) {
                try {
                    int okayRed = Integer.parseInt(ledstrip.get("okayRed").toString());
                    int okayGreen = Integer.parseInt(ledstrip.get("okayGreen").toString());
                    int okayBlue = Integer.parseInt(ledstrip.get("okayBlue").toString());
                    Color okay = new Color(okayRed, okayGreen, okayBlue);

                    int warningRed = Integer.parseInt(ledstrip.get("warningRed").toString());
                    int warningGreen = Integer.parseInt(ledstrip.get("warningGreen").toString());
                    int warningBlue = Integer.parseInt(ledstrip.get("warningBlue").toString());
                    Color warning = new Color(warningRed, warningGreen, warningBlue);

                    int criticalRed = Integer.parseInt(ledstrip.get("criticalRed").toString());
                    int criticalGreen = Integer.parseInt(ledstrip.get("criticalGreen").toString());
                    int criticalBlue = Integer.parseInt(ledstrip.get("criticalBlue").toString());
                    Color critical = new Color(criticalRed, criticalGreen, criticalBlue);

                    Map<String, Color> colors = new HashMap<>();

                    colors.put("okay", okay);
                    colors.put("warning", warning);
                    colors.put("critical", critical);

                    customStatusColors.put(name, colors);
                } catch (NumberFormatException e) {
                    LOG.error("Failed to load config for LED Strip {}: colors are not properly formatted.", name);
                    HealthController.setHealth(Status.WARNING);
                    LEDStripStatus = Status.WARNING;
                }
            }
        }
    }


    public void loadJenkinsServers() throws IOException {

        LOG.info("Loading Jenkins servers");

        List<Map<String, Object>> servers = mapper.readValue(new File(jenkinsServerConfigFile),
                new TypeReference<List<Map<String, Object>>>() {
                });

        for (Map server : servers) {
            jenkinsConfig.putServer(server.get("url").toString(), server.get("user").toString(),
                server.get("key").toString());
        }
    }


    public void loadJenkinsConfig() throws IOException {

        loadJenkinsConfig("jenkins.json");
    }


    /**
     * Load jenkins config.
     *
     * @throws  IOException  the iO exception
     */
    public void loadJenkinsConfig(String file) throws IOException {

        LOG.info("Loading Jenkins configuration");

        Map<String, List<Map<String, Object>>> jenkinsConfigData = mapper.readValue(new File(configDir + file),
                new TypeReference<Map<String, List<Map<String, Object>>>>() {
                }); // fetch Jenkins configuration data...

        jenkinsConfig.reset();

        // if there was no WARNING from the LED Strip Config and no CRITICAL from anywhere, we can safely assume any WARNING came from here
        if (LEDStripStatus == Status.OKAY && HealthController.getHealth() != Status.CRITICAL) {
            HealthController.setHealth(Status.OKAY);
        }

        // ... deserialize the data manually
        for (String server : jenkinsConfigData.keySet()) { // iterate over all the servers

            for (Map line : jenkinsConfigData.get(server)) { // get each configuration line for each server

                String jobName = line.get("name").toString();
                String ledstrip = line.get("ledstrip").toString();

                OutputLEDStripDomain outputLEDStripDomain = outputLEDStripRepository.findByName(ledstrip.toLowerCase()); // names are always lowercase

                OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripDomain);

                if (outputLEDStrip != null) {
                    Map<String, Color> colors = customStatusColors.get(ledstrip);

                    if (colors != null) {
                        jenkinsConfig.put(server, jobName,
                            singleStatusOnLEDStripRegistry.get(outputLEDStrip, colors.get("okay"),
                                colors.get("warning"), colors.get("critical")));
                    } else {
                        jenkinsConfig.put(server, jobName, singleStatusOnLEDStripRegistry.get(outputLEDStrip));
                    }
                } else {
                    LOG.warn("Ledstrip {} does not exist.", ledstrip);

                    if (HealthController.getHealth() != Status.CRITICAL) {
                        HealthController.setHealth(Status.WARNING);
                    }
                }
            }
        }
    }
}
