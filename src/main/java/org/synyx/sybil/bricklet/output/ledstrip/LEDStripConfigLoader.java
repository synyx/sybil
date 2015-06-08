package org.synyx.sybil.bricklet.output.ledstrip;

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
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
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

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(LEDStripConfigLoader.class);

    // The place where the config files lie, taken from the injected environment (and thus ultimately a properties file)
    private String configDir;

    // Jackson ObjectMapper, maps JSON to Java Objects
    private ObjectMapper mapper;

    // Registers bricklets' names to make sure they are unique
    private BrickletNameRegistry brickletNameRegistry;

    // The Repository to save LEDStrip configuration data
    private LEDStripRepository LEDStripRepository;

    // The Repository to save Brick configuration data
    private BrickRepository brickRepository;

    // Map saving the custom status colors for SingleStatusOnLEDStrips
    private LEDStripCustomColors ledStripCustomColors;

    @Autowired
    public LEDStripConfigLoader(ObjectMapper mapper, BrickletNameRegistry brickletNameRegistry,
        org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository LEDStripRepository,
        BrickRepository brickRepository, LEDStripCustomColors ledStripCustomColors, Environment environment) {

        this.mapper = mapper;
        this.brickletNameRegistry = brickletNameRegistry;
        this.LEDStripRepository = LEDStripRepository;
        this.brickRepository = brickRepository;
        this.ledStripCustomColors = ledStripCustomColors;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    /**
     * Load LED Strip configuration.
     */
    public void loadLEDStripConfig() {

        if (HealthController.getHealth() == Status.OKAY) {
            try {
                LOG.info("Loading LED Strip configuration");

                List<Map<String, Object>> ledstrips = mapper.readValue(new File(configDir + "ledstrips.json"),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                LEDStripRepository.deleteAll();

                for (Map ledstrip : ledstrips) { // ... deserialize the data manually

                    String name = ledstrip.get("name").toString();

                    if (brickletNameRegistry.contains(name)) {
                        LOG.error("Failed to load config for LED Strip {}: Name is not unique.", name);
                        HealthController.setHealth(Status.WARNING, "loadLEDStripConfig");

                        break;
                    }

                    brickletNameRegistry.add(name);

                    String uid = ledstrip.get("uid").toString();

                    try {
                        int length = Integer.parseInt(ledstrip.get("length").toString());

                        BrickDomain brick = brickRepository.findByName(ledstrip.get("brick").toString()); // fetch the corresponding bricks from the repo

                        if (brick != null) { // if there was corresponding brick found in the repo...
                            LEDStripRepository.save(new LEDStripDomain(name, uid, length, brick)); // ... save the LED Strip.
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

                            ledStripCustomColors.put(name, colors);
                        } catch (NumberFormatException e) {
                            LOG.error("Failed to load config for LED Strip {}: colors are not properly formatted.",
                                name);
                            HealthController.setHealth(Status.WARNING, "loadLEDStripConfig");
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Error loading ledstrips.json: {}", e.toString());
                HealthController.setHealth(Status.CRITICAL, "loadLEDStripConfig");
            }
        }
    }
}
