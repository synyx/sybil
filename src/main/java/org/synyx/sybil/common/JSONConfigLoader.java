package org.synyx.sybil.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Map;


/**
 * JSONConfigLoader. Loads complete or partial configurations from JSON files.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class JSONConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(JSONConfigLoader.class); // Logger

    private ObjectMapper mapper = new ObjectMapper(); // JSON<->POJO Mapper

    private String configDir; // The place where the config files lie, taken from the injected environment (and thus ultimately a properties file)

    private BrickDomain[] bricks; // The configuration data for bricks

    private BrickRepository brickRepository; // The Repository to save said configuration data

    private Map<String, Object>[] ledstrips; // The configuration data for LED Strips

    private OutputLEDStripRepository outputLEDStripRepository; // The Repository to save said configuration data

    /**
     * Instantiates a new JSON config loader. Parameters are autowired.
     *
     * @param  brickRepository  the Brick repository
     * @param  outputLEDStripRepository  the LED Strip repository
     * @param  env  the Environment
     */
    @Autowired
    public JSONConfigLoader(BrickRepository brickRepository, OutputLEDStripRepository outputLEDStripRepository,
        Environment env) {

        this.brickRepository = brickRepository;
        this.outputLEDStripRepository = outputLEDStripRepository;
        configDir = env.getProperty("path.to.configfiles");
    }

    /**
     * Load the complete configuration from JSON files.
     */
    public void loadConfig() {

        brickRepository.deleteAll();

        try {
            bricks = mapper.readValue(new File(configDir + "bricks.json"), BrickDomain[].class); // fetch brick configuration data...
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Error Handling
        }

        brickRepository.save(Arrays.asList(bricks)); // ... simply dump them into the database

        outputLEDStripRepository.deleteAll();

        try {
            ledstrips = mapper.readValue(new File(configDir + "ledstrips.json"), Map[].class); // fetch LED Strip configuration data...
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Error Handling
        }

        for (Map ledstrip : ledstrips) { // ... deserialize the data manually

            String name = ledstrip.get("name").toString();
            String uid = ledstrip.get("uid").toString();
            int length = (int) ledstrip.get("length");
            BrickDomain brick = brickRepository.findByHostname(ledstrip.get("brick").toString()); // fetch the corresponding bricks fromt the repo

            if (brick != null) { // if there was corresponding brick found in the repo...
                outputLEDStripRepository.save(new OutputLEDStripDomain(name, uid, length, brick)); // ... save the LED Strip.
            } else { // if not...
                LOG.error("Brick " + ledstrip.get("brick").toString() + " does not exist."); // ... error! TODO: Error Handling
            }
        }
    }
}
