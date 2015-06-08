package org.synyx.sybil.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.bricklet.BrickletNameRegistry;
import org.synyx.sybil.bricklet.input.button.ButtonSensorRegistry;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;
import org.synyx.sybil.bricklet.input.button.database.ButtonRepository;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceSensorRegistry;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorRepository;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripCustomColors;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.SingleStatusOnLEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;
import org.synyx.sybil.bricklet.output.relay.database.RelayRepository;
import org.synyx.sybil.jenkins.config.JenkinsConfig;
import org.synyx.sybil.jenkins.domain.Status;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
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

    // The Repository to save LEDStrip configuration data
    private LEDStripRepository LEDStripRepository;

    // This fetches the actual LED Strip objects for given config data
    private LEDStripRegistry LEDStripRegistry;

    // Fetches one SingleStatusOnLEDStrip for each LED Strip
    private SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry;

    // The Repository to save Relay configuration data
    private RelayRepository relayRepository;

    // The Repository to save IlluminanceSensor configuration data
    private IlluminanceSensorRepository illuminanceSensorRepository;

    // The Repository to save Button configuration data
    private ButtonRepository buttonRepository;

    // Fetches & Configures illuminance sensors
    private IlluminanceSensorRegistry illuminanceSensorRegistry;

    // Fetches & Configures buttons
    private ButtonSensorRegistry buttonSensorRegistry;

    // The object that saves the Jenkins servers and job configurations
    private JenkinsConfig jenkinsConfig;

    // Registers bricklets' names to make sure they are unique
    private BrickletNameRegistry brickletNameRegistry;

    // Map saving the custom status colors for SingleStatusOnLEDStrips
    private LEDStripCustomColors ledStripCustomColors;

    /**
     * Instantiates a new JSON config loader.
     *
     * @param  brickRepository  The Brick repository
     * @param  LEDStripRepository  The LEDStrip repository
     * @param  env  The Environment (provided by Spring, contains the configuration read from config.properties)
     * @param  LEDStripRegistry  the LEDStrip registry
     * @param  jenkinsConfig  the jenkins configuration
     * @param  singleStatusOnLEDStripRegistry  the SingleStatusOnLEDStrip registry
     * @param  relayRepository  the output relay repository
     * @param  illuminanceSensorRepository  the input sensor repository
     * @param  illuminanceSensorRegistry  the illuminance sensor registry
     * @param  buttonSensorRegistry  the button sensor registry
     */
    @Autowired
    public ConfigLoader(BrickRepository brickRepository, LEDStripRepository LEDStripRepository, Environment env,
        LEDStripRegistry LEDStripRegistry, JenkinsConfig jenkinsConfig,
        SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry, RelayRepository relayRepository,
        IlluminanceSensorRepository illuminanceSensorRepository, ButtonRepository buttonRepository,
        IlluminanceSensorRegistry illuminanceSensorRegistry, ButtonSensorRegistry buttonSensorRegistry,
        BrickletNameRegistry brickletNameRegistry, LEDStripCustomColors ledStripCustomColors) {

        this.brickRepository = brickRepository;
        this.LEDStripRepository = LEDStripRepository;
        configDir = env.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = env.getProperty("jenkins.configfile");
        this.LEDStripRegistry = LEDStripRegistry;
        this.jenkinsConfig = jenkinsConfig;
        this.singleStatusOnLEDStripRegistry = singleStatusOnLEDStripRegistry;
        this.relayRepository = relayRepository;
        this.illuminanceSensorRepository = illuminanceSensorRepository;
        this.illuminanceSensorRegistry = illuminanceSensorRegistry;
        this.buttonSensorRegistry = buttonSensorRegistry;
        this.buttonRepository = buttonRepository;
        this.brickletNameRegistry = brickletNameRegistry;
        this.ledStripCustomColors = ledStripCustomColors;
    }

    /**
     * Load the complete configuration from JSON files.
     */
    public void loadConfig() {

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                loadRelayConfig();
            } catch (IOException e) {
                LOG.error("Error loading relays.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadRelayConfig");
            }
        }

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                loadSensorConfig();
            } catch (IOException e) {
                LOG.error("Error loading sensors.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");
            }
        }

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                loadJenkinsServers();
            } catch (IOException e) {
                LOG.error("Error loading jenkinsservers.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadJenkinsServers");
            }
        }

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                loadJenkinsConfig();
            } catch (IOException e) {
                LOG.error("Error loading jenkins.json: {}", e.toString());
                HealthController.setHealth(Status.WARNING, "loadJenkinsConfig");
            }
        }
    }


    /**
     * Load Relay configuration.
     *
     * @throws  IOException  the iO exception
     */
    public void loadRelayConfig() throws IOException {

        LOG.info("Loading Relay configuration");

        List<Map<String, Object>> relays = mapper.readValue(new File(configDir + "relays.json"),
                new TypeReference<List<Map<String, Object>>>() {
                });

        relayRepository.deleteAll();

        for (Map relay : relays) { // ... deserialize the data manually

            String name = relay.get("name").toString();

            if (brickletNameRegistry.contains(name)) {
                LOG.error("Failed to load config for Relay {}: Name is not unique.", name);
                HealthController.setHealth(Status.WARNING, "loadRelayConfig");

                break;
            }

            brickletNameRegistry.add(name);

            String uid = relay.get("uid").toString();

            BrickDomain brick = brickRepository.findByName(relay.get("brick").toString()); // fetch the corresponding bricks from the repo

            if (brick != null) { // if there was corresponding brick found in the repo...
                relayRepository.save(new RelayDomain(name, uid, brick)); // ... save the relay.
            } else { // if not...
                LOG.error("Brick {} does not exist.", relay.get("brick").toString()); // ... error!
                HealthController.setHealth(Status.WARNING, "loadRelayConfig");
            }
        }
    }


    /**
     * Load Sensor configuration.
     *
     * @throws  IOException  the iO exception
     */
    public void loadSensorConfig() throws IOException {

        LOG.info("Loading Sensor configuration");

        List<Map<String, Object>> sensors = mapper.readValue(new File(configDir + "sensors.json"),
                new TypeReference<List<Map<String, Object>>>() {
                });

        illuminanceSensorRepository.deleteAll();
        buttonRepository.deleteAll();

        for (Map sensor : sensors) { // ... deserialize the data manually

            String name = sensor.get("name").toString();

            if (brickletNameRegistry.contains(name)) {
                LOG.error("Failed to load config for Sensor {}: Name is not unique.", name);
                HealthController.setHealth(Status.WARNING, "loadSensorConfig");

                break;
            }

            brickletNameRegistry.add(name);

            String uid = sensor.get("uid").toString();

            String type = sensor.get("type").toString();

            int threshold = 0;
            double multiplier = 0.1;
//            int timeout = 0;
            short pins = 0b0000;

            try {
                if (sensor.get("threshold") != null) {
                    threshold = Integer.parseInt(sensor.get("threshold").toString());
                }

                if (sensor.get("multiplier") != null) {
                    multiplier = Double.parseDouble(sensor.get("multiplier").toString());
                }

//                if (sensor.get("timeout") != null) {
//                    timeout = Integer.parseInt(sensor.get("timeout").toString());
//                }

                if (sensor.get("pins") != null) {
                    pins = (short) Integer.parseInt(sensor.get("pins").toString(), 2);
                }
            } catch (NumberFormatException e) {
                LOG.error("Failed to load config for sensor {}: options are not properly formatted.", name);
                HealthController.setHealth(Status.WARNING, "loadSensorConfig");
            }

            List<String> outputs = new ArrayList<>();

            if (sensor.get("outputs") instanceof ArrayList) {
                ArrayList rawArrayList = (ArrayList) sensor.get("outputs");

                for (Object output : rawArrayList) {
                    outputs.add(output.toString());
                }
            }

            BrickDomain brick = brickRepository.findByName(sensor.get("brick").toString()); // fetch the corresponding bricks from the repo

            IlluminanceSensorDomain illuminanceSensorDomain = null;
            ButtonDomain buttonDomain = null;

            if (brick != null) { // if there was corresponding brick found in the repo...

                if (type.equals("luminance")) {
                    illuminanceSensorDomain = illuminanceSensorRepository.save(new IlluminanceSensorDomain(name, uid,
                                threshold, multiplier, outputs, brick)); // ... save the sensor
                }

                if (type.equals("button")) {
                    buttonDomain = buttonRepository.save(new ButtonDomain(name, uid, pins, outputs, brick)); // ... save the sensor
                }
            } else { // if not...
                LOG.error("Brick {} does not exist.", sensor.get("brick").toString()); // ... error!
                HealthController.setHealth(Status.WARNING, "loadSensorConfig");
            }

            if (illuminanceSensorDomain != null) {
                illuminanceSensorRegistry.get(illuminanceSensorDomain);
            } else if (buttonDomain != null) {
                buttonSensorRegistry.get(buttonDomain);
            }
        }
    }


    /**
     * Load jenkins servers.
     *
     * @throws  IOException  the iO exception
     */
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


    /**
     * Load jenkins config.
     *
     * @throws  IOException  the iO exception
     */
    public void loadJenkinsConfig() throws IOException {

        loadJenkinsConfig("jenkins.json");
    }


    /**
     * Load jenkins config.
     *
     * @param  file  the file
     *
     * @throws  IOException  the iO exception
     */
    public void loadJenkinsConfig(String file) throws IOException {

        LOG.info("Loading Jenkins configuration");

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

                LEDStrip LEDStrip = LEDStripRegistry.get(LEDStripDomain);

                if (LEDStrip != null) {
                    Map<String, Color> colors = ledStripCustomColors.get(ledstrip);

                    if (colors != null) {
                        jenkinsConfig.put(server, jobName,
                            singleStatusOnLEDStripRegistry.get(LEDStrip, colors.get("okay"), colors.get("warning"),
                                colors.get("critical")));
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
    }
}
