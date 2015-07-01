package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.BrickletNameService;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.jenkins.domain.Status;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * LEDStripConfigLoader.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class LEDStripConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(LEDStripConfigLoader.class);

    private String configDir;
    private ObjectMapper mapper;
    private BrickletNameService brickletNameRegistry;
    private LEDStripService ledStripService;
    private BrickService brickService;
    private LEDStripCustomColors ledStripCustomColors;

    @Autowired
    public LEDStripConfigLoader(ObjectMapper mapper, BrickletNameService brickletNameRegistry,
        BrickService brickService, LEDStripCustomColors ledStripCustomColors, Environment environment,
        LEDStripService ledStripRepository) {

        this.mapper = mapper;
        this.brickletNameRegistry = brickletNameRegistry;
        this.brickService = brickService;
        this.ledStripCustomColors = ledStripCustomColors;
        ledStripService = ledStripRepository;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    public void loadLEDStripConfig() {

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                LOG.info("Loading LED Strip configuration");

                // read a List of String->Object Maps from the config file, one Map per LED strip.
                List<Map<String, Object>> ledstrips = mapper.readValue(new File(configDir + "ledstrips.json"),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                ledStripService.deleteAllDomains();
                ledStripCustomColors.clear();

                deserialize(ledstrips);
            } catch (IOException exception) {
                logError("Error loading ledstrips.json: " + exception.getMessage());
            }
        }
    }


    private void logError(String message) {

        LOG.error(message);
        HealthController.setHealth(Status.CRITICAL, "loadLEDStripConfig");
    }


    private void deserialize(List<Map<String, Object>> ledstrips) {

        for (Map ledstrip : ledstrips) {
            try {
                getAndSaveValues(ledstrip);
            } catch (LoadFailedException exception) {
                logWarning(exception);
            }
        }
    }


    private void logWarning(Exception exception) {

        LOG.error(exception.getMessage());
        HealthController.setHealth(Status.WARNING, "loadLEDStripConfig");
    }


    private void getAndSaveValues(Map ledstrip) {

        String name = getString(ledstrip, "name");
        String uid = getString(ledstrip, "uid");
        int length = getInt(ledstrip, "length");
        BrickDomain brick = brickService.getDomain(getString(ledstrip, "brick"));

        if (brickletNameRegistry.contains(name))
            throw new LoadFailedException("Failed to load config for LED strip " + name + ": Name is not unique.");

        brickletNameRegistry.add(name);

        ledStripService.saveDomain(new LEDStripDomain(name, uid, length, brick));

        registerCustomColors(ledstrip);
    }


    private String getString(Map ledstrip, String key) {

        Object value = ledstrip.get(key);

        if (value == null) {
            throw new LoadFailedException("Failed to load config for LED strip " + ledstrip.get("name")
                + ": Value missing");
        }

        return value.toString();
    }


    private int getInt(Map ledstrip, String key) {

        int value;

        try {
            value = Integer.parseInt(getString(ledstrip, key));
        } catch (NumberFormatException exception) {
            throw new LoadFailedException("Failed to load config for LED strip " + getString(ledstrip, "name")
                + ": " + key + " is not an integer.");
        }

        return value;
    }


    private void registerCustomColors(Map ledstrip) {

        if (hasCustomColors(ledstrip)) {
            String name = getString(ledstrip, "name");
            Map<String, Color> colors = new HashMap<>();

            for (String status : new String[] { "okay", "warning", "critical" }) {
                colors.put(status, createCustomColor(ledstrip, status));
            }

            ledStripCustomColors.put(name, colors);
        }
    }


    private boolean hasCustomColors(Map ledstrip) {

        return ledstrip.get("okayRed") != null;
    }


    private Color createCustomColor(Map ledstrip, String status) {

        int red = getInt(ledstrip, status + "Red");
        int green = getInt(ledstrip, status + "Green");
        int blue = getInt(ledstrip, status + "Blue");

        return new Color(red, green, blue);
    }
}
