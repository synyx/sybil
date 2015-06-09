package org.synyx.sybil.bricklet.input;

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
import org.synyx.sybil.jenkins.domain.Status;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * SensorConfigLoader.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class SensorConfigLoader {

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(SensorConfigLoader.class);

    // Jackson ObjectMapper, maps JSON to Java Objects
    private ObjectMapper mapper;

    // The place where the config files lie, taken from the injected environment (and thus ultimately a properties file)
    private String configDir;

    // The Repository to save IlluminanceSensor configuration data
    private IlluminanceSensorRepository illuminanceSensorRepository;

    // The Repository to save Button configuration data
    private ButtonRepository buttonRepository;

    // Fetches & Configures illuminance sensors
    private IlluminanceSensorRegistry illuminanceSensorRegistry;

    // Fetches & Configures buttons
    private ButtonSensorRegistry buttonSensorRegistry;

    // Registers bricklets' names to make sure they are unique
    private BrickletNameRegistry brickletNameRegistry;

    // The Repository to save Brick configuration data
    private BrickRepository brickRepository;

    @Autowired
    public SensorConfigLoader(ObjectMapper mapper, IlluminanceSensorRepository illuminanceSensorRepository,
        ButtonRepository buttonRepository, IlluminanceSensorRegistry illuminanceSensorRegistry,
        ButtonSensorRegistry buttonSensorRegistry, BrickletNameRegistry brickletNameRegistry,
        BrickRepository brickRepository, Environment environment) {

        this.mapper = mapper;
        this.illuminanceSensorRepository = illuminanceSensorRepository;
        this.buttonRepository = buttonRepository;
        this.illuminanceSensorRegistry = illuminanceSensorRegistry;
        this.buttonSensorRegistry = buttonSensorRegistry;
        this.brickletNameRegistry = brickletNameRegistry;
        this.brickRepository = brickRepository;
        configDir = environment.getProperty("path.to.configfiles");
    }

    public void loadSensorConfig() {

        if (HealthController.getHealth() == Status.OKAY) {
            try {
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
                        HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");

                        break;
                    }

                    brickletNameRegistry.add(name);

                    String uid = sensor.get("uid").toString();

                    String type = sensor.get("type").toString();

                    int threshold = 0;
                    double multiplier = 0.1;
//                    int timeout = 0;
                    short pins = 0b0000;

                    try {
                        if (sensor.get("threshold") != null) {
                            threshold = Integer.parseInt(sensor.get("threshold").toString());
                        }

                        if (sensor.get("multiplier") != null) {
                            multiplier = Double.parseDouble(sensor.get("multiplier").toString());
                        }

//                        if (sensor.get("timeout") != null) {
//                            timeout = Integer.parseInt(sensor.get("timeout").toString());
//                        }

                        if (sensor.get("pins") != null) {
                            pins = (short) Integer.parseInt(sensor.get("pins").toString(), 2); // parse from binary
                        }
                    } catch (NumberFormatException e) {
                        LOG.error("Failed to load config for sensor {}: options are not properly formatted.", name);
                        HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");
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
                            illuminanceSensorDomain = illuminanceSensorRepository.save(new IlluminanceSensorDomain(name,
                                        uid, threshold, multiplier, outputs, brick)); // ... save the sensor
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
            } catch (IOException e) {
                LOG.error("Error loading sensors.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");
            }
        }
    }
}
