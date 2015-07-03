package org.synyx.sybil.bricklet.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.BrickletNameService;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceService;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
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

    private static final Logger LOG = LoggerFactory.getLogger(SensorConfigLoader.class);
    private ObjectMapper mapper;
    private String configDir;
    private IlluminanceService illuminanceService;
    private BrickletNameService brickletNameRegistry;
    private BrickService brickService;

    @Autowired
    public SensorConfigLoader(ObjectMapper mapper, IlluminanceService illuminanceService,
        BrickletNameService brickletNameRegistry, BrickService brickService, Environment environment) {

        this.mapper = mapper;
        this.illuminanceService = illuminanceService;
        this.brickletNameRegistry = brickletNameRegistry;
        this.brickService = brickService;
        configDir = environment.getProperty("path.to.configfiles");
    }

    public void loadSensorConfig() {

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                LOG.info("Loading Sensor configuration");

                List<Map<String, Object>> sensors = mapper.readValue(new File(configDir + "sensors.json"),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                illuminanceService.deleteAllDomains();

                for (Map sensor : sensors) {
                    String name = sensor.get("name").toString();

                    if (brickletNameRegistry.contains(name)) {
                        LOG.error("Failed to load config for Sensor {}: Name is not unique.", name);
                        HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");

                        break;
                    }

                    brickletNameRegistry.add(name);

                    String uid = sensor.get("uid").toString();

                    String type = sensor.get("type").toString();

                    int threshold = getThreshold(sensor);

                    double multiplier = getMultiplier(sensor);

                    List<String> outputs = getOutputs(sensor);

                    BrickDomain brick = brickService.getDomain(sensor.get("brick").toString());

                    if ("luminance".equals(type)) {
                        IlluminanceSensorDomain illuminanceSensorDomain = illuminanceService.saveDomain(
                                new IlluminanceSensorDomain(name, uid, threshold, multiplier, outputs, brick));
                        illuminanceService.getIlluminanceSensor(illuminanceSensorDomain);
                    }
                }
            } catch (IOException e) {
                LOG.error("Error loading sensors.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");
            }
        }
    }


    private int getThreshold(Map sensor) {

        int threshold = 0;

        try {
            if (sensor.get("threshold") == null) {
                threshold = 0;
            } else {
                threshold = Integer.parseInt(sensor.get("threshold").toString());
            }
        } catch (NumberFormatException e) {
            LOG.error("Failed to load config for sensor {}: options are not properly formatted.", sensor.get("name"));
            HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");
        }

        return threshold;
    }


    private double getMultiplier(Map sensor) {

        double multiplier = 0;

        try {
            if (sensor.get("multiplier") == null) {
                multiplier = 0;
            } else {
                multiplier = Double.parseDouble(sensor.get("multiplier").toString());
            }
        } catch (NumberFormatException e) {
            LOG.error("Failed to load config for sensor {}: options are not properly formatted.", sensor.get("name"));
            HealthController.setHealth(Status.CRITICAL, "loadSensorConfig");
        }

        return multiplier;
    }


    private List<String> getOutputs(Map sensor) {

        List<String> outputs = new ArrayList<>();

        if (sensor.get("outputs") instanceof ArrayList) {
            ArrayList rawArrayList = (ArrayList) sensor.get("outputs");

            for (Object output : rawArrayList) {
                outputs.add(output.toString());
            }
        }

        return outputs;
    }
}
