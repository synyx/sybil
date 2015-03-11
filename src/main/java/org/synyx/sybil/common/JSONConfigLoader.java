package org.synyx.sybil.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.common.jenkins.JenkinsConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.OutputLEDStripRegistry;
import org.synyx.sybil.out.StatusesOnLEDStrip;

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

    private OutputLEDStripRegistry outputLEDStripRegistry; // This fetches the actual LED Strip objects for given config data

    private JenkinsConfig jenkinsConfig;

    /**
     * Instantiates a new JSON config loader. Parameters are autowired.
     *
     * @param  brickRepository  the Brick repository
     * @param  outputLEDStripRepository  the LED Strip repository
     * @param  env  the Environment
     */
    @Autowired
    public JSONConfigLoader(BrickRepository brickRepository, OutputLEDStripRepository outputLEDStripRepository,
        Environment env, OutputLEDStripRegistry outputLEDStripRegistry, JenkinsConfig jenkinsConfig) {

        this.brickRepository = brickRepository;
        this.outputLEDStripRepository = outputLEDStripRepository;
        configDir = env.getProperty("path.to.configfiles");
        this.outputLEDStripRegistry = outputLEDStripRegistry;
        this.jenkinsConfig = jenkinsConfig;
    }

    /**
     * Load the complete configuration from JSON files.
     */
    public void loadConfig() throws IOException {

        loadBricksConfig();

        loadLEDStripConfig();

        loadJenkinsConfig();
    }


    public void loadBricksConfig() throws IOException {

        LOG.info("Loading Brick configuration");

        bricks = mapper.readValue(new File(configDir + "bricks.json"), BrickDomain[].class); // fetch brick configuration data...

        brickRepository.deleteAll();

        brickRepository.save(Arrays.asList(bricks)); // ... simply dump them into the database
    }


    public void loadLEDStripConfig() throws IOException {

        LOG.info("Loading LED Strip configuration");

        ledstrips = mapper.readValue(new File(configDir + "ledstrips.json"), Map[].class); // fetch LED Strip configuration data...

        outputLEDStripRepository.deleteAll();

        for (Map ledstrip : ledstrips) { // ... deserialize the data manually

            String name = ledstrip.get("name").toString();
            String uid = ledstrip.get("uid").toString();
            int length = (int) ledstrip.get("length"); // TODO: Error Handling
            BrickDomain brick = brickRepository.findByHostname(ledstrip.get("brick").toString()); // fetch the corresponding bricks fromt the repo

            if (brick != null) { // if there was corresponding brick found in the repo...
                outputLEDStripRepository.save(new OutputLEDStripDomain(name, uid, length, brick)); // ... save the LED Strip.
            } else { // if not...
                LOG.error("Brick " + ledstrip.get("brick").toString() + " does not exist."); // ... error! TODO: Error Handling
            }
        }
    }


    public void loadJenkinsConfig() throws IOException {

        LOG.info("Loading Jenkins configuration");

        Map<String, Object>[] jenkins = mapper.readValue(new File(configDir + "jenkins.json"), Map[].class); // fetch Jenkins configuration data...

        for (Map line : jenkins) { // ... deserialize the data manually

            String name = line.get("name").toString();
            String ledstrip = line.get("ledstrip").toString();

            OutputLEDStripDomain outputLEDStripDomain = outputLEDStripRepository.findByName(ledstrip.toLowerCase()); // names are always lowercase

            OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripDomain);

            if (outputLEDStrip != null) {
                jenkinsConfig.put(name, new StatusesOnLEDStrip(outputLEDStrip));
            } else {
                LOG.error("Ledstrip " + ledstrip + " does not exist.");
            }
        }
    }
}
