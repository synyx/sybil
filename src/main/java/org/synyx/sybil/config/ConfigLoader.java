package org.synyx.sybil.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.brick.BrickRegistry;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.bricklet.input.SensorType;
import org.synyx.sybil.bricklet.input.button.ButtonSensorRegistry;
import org.synyx.sybil.bricklet.input.database.InputSensorDomain;
import org.synyx.sybil.bricklet.input.database.InputSensorRepository;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceSensorRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.OutputLEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.OutputLEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.SingleStatusOnLEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.OutputLEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.OutputLEDStripRepository;
import org.synyx.sybil.bricklet.output.relay.database.OutputRelayDomain;
import org.synyx.sybil.bricklet.output.relay.database.OutputRelayRepository;
import org.synyx.sybil.common.jenkins.JenkinsConfig;
import org.synyx.sybil.in.Status;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

    // The Repository to save OutputRelay configuration data
    private OutputRelayRepository outputRelayRepository;

    // The Repository to save InputSensor configuration data
    private InputSensorRepository inputSensorRepository;

    // Fetches & Configures illuminance sensors
    private IlluminanceSensorRegistry illuminanceSensorRegistry;

    // Fetches & Configures buttons
    private ButtonSensorRegistry buttonSensorRegistry;

    // The object that saves the Jenkins servers and job configurations
    private JenkinsConfig jenkinsConfig;

    // Map saving the custom status colors for SingleStatusOnLEDStrips
    private Map<String, Map<String, Color>> customStatusColors = new HashMap<>();

    // Set saving all the names, making sure, they are all unique
    private Set<String> brickletNames = new HashSet<>();

    // Database service
    private GraphDatabaseService graphDatabaseService;

    private BrickRegistry brickRegistry;

    /**
     * Instantiates a new JSON config loader.
     *
     * @param  brickRepository  The Brick repository
     * @param  outputLEDStripRepository  The OutputLEDStrip repository
     * @param  env  The Environment (provided by Spring, contains the configuration read from config.properties)
     * @param  outputLEDStripRegistry  the OutputLEDStrip registry
     * @param  jenkinsConfig  the jenkins configuration
     * @param  singleStatusOnLEDStripRegistry  the SingleStatusOnLEDStrip registry
     * @param  outputRelayRepository  the output relay repository
     * @param  inputSensorRepository  the input sensor repository
     * @param  graphDatabaseService  the graph database service
     * @param  brickRegistry  the brick registry
     * @param  illuminanceSensorRegistry  the illuminance sensor registry
     * @param  buttonSensorRegistry  the button sensor registry
     */
    @Autowired
    public ConfigLoader(BrickRepository brickRepository, OutputLEDStripRepository outputLEDStripRepository,
        Environment env, OutputLEDStripRegistry outputLEDStripRegistry, JenkinsConfig jenkinsConfig,
        SingleStatusOnLEDStripRegistry singleStatusOnLEDStripRegistry, OutputRelayRepository outputRelayRepository,
        InputSensorRepository inputSensorRepository, GraphDatabaseService graphDatabaseService,
        BrickRegistry brickRegistry, IlluminanceSensorRegistry illuminanceSensorRegistry,
        ButtonSensorRegistry buttonSensorRegistry) {

        this.brickRepository = brickRepository;
        this.outputLEDStripRepository = outputLEDStripRepository;
        configDir = env.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = env.getProperty("jenkins.configfile");
        this.outputLEDStripRegistry = outputLEDStripRegistry;
        this.jenkinsConfig = jenkinsConfig;
        this.singleStatusOnLEDStripRegistry = singleStatusOnLEDStripRegistry;
        this.outputRelayRepository = outputRelayRepository;
        this.inputSensorRepository = inputSensorRepository;
        this.graphDatabaseService = graphDatabaseService;
        this.brickRegistry = brickRegistry;
        this.illuminanceSensorRegistry = illuminanceSensorRegistry;
        this.buttonSensorRegistry = buttonSensorRegistry;
    }

    /**
     * Load the complete configuration from JSON files.
     */
    public void loadConfig() {

        brickletNames.clear();

        try {
            loadBricksConfig();
        } catch (IOException e) {
            LOG.error("Error loading bricks.json: {}", e.toString());
            HealthController.setHealth(Status.CRITICAL, "loadBricksConfig");
        }

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                resetBricks();
            } catch (TimeoutException | NotConnectedException | InterruptedException e) {
                LOG.error("Error resetting bricks: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "resetBricks");
            }
        }

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                loadLEDStripConfig();
            } catch (IOException e) {
                LOG.error("Error loading ledstrips.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadLEDStripConfig");
            }
        }

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
     * Reset all bricks.
     *
     * @throws  TimeoutException  the timeout exception
     * @throws  TimeoutException  the timeout exception
     * @throws  TimeoutException  the timeout exception
     */
    public void resetBricks() throws TimeoutException, NotConnectedException, InterruptedException {

        LOG.info("Resetting bricks");

        List<BrickDomain> bricks;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            bricks = new ArrayList<>(IteratorUtil.asCollection(brickRepository.findAll()));

            // end transaction
            tx.success();
        }

        for (BrickDomain brick : bricks) {
            IPConnection ipConnection = brickRegistry.get(brick);
            BrickMaster brickMaster = new BrickMaster(brick.getUid(), ipConnection);
            brickMaster.reset();
        }

        Thread.sleep(5000);

        brickRegistry.disconnectAll();
    }


    /**
     * Load LED Strip configuration.
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

            if (brickletNames.contains(name)) {
                LOG.error("Failed to load config for LED Strip {}: Name is not unique.", name);
                HealthController.setHealth(Status.WARNING, "loadLEDStripConfig");

                break;
            }

            brickletNames.add(name);

            String uid = ledstrip.get("uid").toString();

            try {
                int length = Integer.parseInt(ledstrip.get("length").toString());

                BrickDomain brick = brickRepository.findByName(ledstrip.get("brick").toString()); // fetch the corresponding bricks from the repo

                if (brick != null) { // if there was corresponding brick found in the repo...
                    outputLEDStripRepository.save(new OutputLEDStripDomain(name, uid, length, brick)); // ... save the LED Strip.
                } else { // if not...
                    LOG.error("Brick {} does not exist.", ledstrip.get("brick").toString()); // ... error!
                    HealthController.setHealth(Status.WARNING, "loadLEDStripConfig");
                }
            } catch (NumberFormatException e) {
                LOG.error("Failed to load config for LED Strip {}: \"length\" is not an integer.", name);
                HealthController.setHealth(Status.WARNING, "loadLEDStripConfig");
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
                    HealthController.setHealth(Status.WARNING, "loadLEDStripConfig");
                }
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

        outputRelayRepository.deleteAll();

        for (Map relay : relays) { // ... deserialize the data manually

            String name = relay.get("name").toString();

            if (brickletNames.contains(name)) {
                LOG.error("Failed to load config for Relay {}: Name is not unique.", name);
                HealthController.setHealth(Status.WARNING, "loadRelayConfig");

                break;
            }

            brickletNames.add(name);

            String uid = relay.get("uid").toString();

            BrickDomain brick = brickRepository.findByName(relay.get("brick").toString()); // fetch the corresponding bricks from the repo

            if (brick != null) { // if there was corresponding brick found in the repo...
                outputRelayRepository.save(new OutputRelayDomain(name, uid, brick)); // ... save the relay.
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

        inputSensorRepository.deleteAll();

        for (Map sensor : sensors) { // ... deserialize the data manually

            String name = sensor.get("name").toString();

            if (brickletNames.contains(name)) {
                LOG.error("Failed to load config for Sensor {}: Name is not unique.", name);
                HealthController.setHealth(Status.WARNING, "loadSensorConfig");

                break;
            }

            brickletNames.add(name);

            String uid = sensor.get("uid").toString();

            SensorType type = SensorType.valueOf(sensor.get("type").toString().toUpperCase());

            int threshold = 0;
            double multiplier = 0.1;
            int timeout = 0;
            short pins = 0b0000;

            try {
                if (sensor.get("threshold") != null) {
                    threshold = Integer.parseInt(sensor.get("threshold").toString());
                }

                if (sensor.get("multiplier") != null) {
                    multiplier = Double.parseDouble(sensor.get("multiplier").toString());
                }

                if (sensor.get("timeout") != null) {
                    timeout = Integer.parseInt(sensor.get("timeout").toString());
                }

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

            InputSensorDomain domain = null;

            if (brick != null) { // if there was corresponding brick found in the repo...
                domain = inputSensorRepository.save(new InputSensorDomain(name, uid, type, threshold, multiplier,
                            timeout, pins, outputs, brick)); // ... save the sensor
            } else { // if not...
                LOG.error("Brick {} does not exist.", sensor.get("brick").toString()); // ... error!
                HealthController.setHealth(Status.WARNING, "loadSensorConfig");
            }

            if (type == SensorType.LUMINANCE && domain != null) {
                illuminanceSensorRegistry.get(domain);
            } else if (type == SensorType.BUTTON) {
                buttonSensorRegistry.get(domain);
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
                        HealthController.setHealth(Status.WARNING, "loadJenkinsConfig");
                    }
                }
            }
        }
    }
}
