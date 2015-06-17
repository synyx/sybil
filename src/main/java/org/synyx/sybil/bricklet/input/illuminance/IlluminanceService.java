package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.BrickletService;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorRepository;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * LEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class IlluminanceService implements BrickletService {

    private static final Logger LOG = LoggerFactory.getLogger(IlluminanceService.class);

    private Map<IlluminanceSensorDomain, BrickletAmbientLight> illuminanceSensors = new HashMap<>();
    private BrickService brickService;
    private LEDStripService LEDStripService;
    private LEDStripRepository LEDStripRepository;
    private IlluminanceSensorRepository illuminanceSensorRepository;
    private GraphDatabaseService graphDatabaseService;

    /**
     * Instantiates a new Illuminance sensor registry.
     *
     * @param  brickService  the brick registry
     * @param  LEDStripService  the output lED strip registry
     * @param  LEDStripRepository  the output lED strip repository
     * @param  illuminanceSensorRepository  the illuminance sensor repository
     * @param  graphDatabaseService  the graph database service
     */
    @Autowired
    public IlluminanceService(BrickService brickService, LEDStripService LEDStripService,
        LEDStripRepository LEDStripRepository, IlluminanceSensorRepository illuminanceSensorRepository,
        GraphDatabaseService graphDatabaseService) {

        this.brickService = brickService;
        this.LEDStripService = LEDStripService;
        this.LEDStripRepository = LEDStripRepository;
        this.illuminanceSensorRepository = illuminanceSensorRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    /**
     * Save domain.
     *
     * @param  illuminanceDomain  the illuminance domain
     *
     * @return  the illuminance sensor domain
     */
    public IlluminanceSensorDomain saveDomain(IlluminanceSensorDomain illuminanceDomain) {

        return illuminanceSensorRepository.save(illuminanceDomain);
    }


    /**
     * Gets domain.
     *
     * @param  name  the name
     *
     * @return  the domain
     */
    public IlluminanceSensorDomain getDomain(String name) {

        return illuminanceSensorRepository.findByName(name);
    }


    /**
     * Gets all domains.
     *
     * @return  the all domains
     */
    public List<IlluminanceSensorDomain> getAllDomains() {

        List<IlluminanceSensorDomain> illuminanceSensorDomains;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all sensors from database and cast them into a list so that they're actually fetched
            illuminanceSensorDomains = new ArrayList<>(IteratorUtil.asCollection(
                        illuminanceSensorRepository.findAll()));

            // end transaction
            tx.success();
        }

        return illuminanceSensorDomains;
    }


    /**
     * Delete all domains.
     */
    public void deleteAllDomains() {

        illuminanceSensorRepository.deleteAll();
    }


    /**
     * Get a BrickletAmbientLight object, instantiate a new one if necessary.
     *
     * @param  illuminanceSensorDomain  The bricklet's domain from the database.
     *
     * @return  The actual BrickletAmbientLight object.
     */
    public BrickletAmbientLight getIlluminanceSensor(IlluminanceSensorDomain illuminanceSensorDomain) {

        if (illuminanceSensorDomain == null) {
            return null;
        }

        LOG.debug("Setting up sensor {}.", illuminanceSensorDomain.getName());

        if (!illuminanceSensors.containsKey(illuminanceSensorDomain)) {
            BrickletAmbientLight brickletAmbientLight;

            try {
                // get the connection to the Brick, passing the BrickDomain and the calling object
                IPConnection ipConnection = brickService.getIPConnection(illuminanceSensorDomain.getBrickDomain(),
                        this);

                if (ipConnection != null) {
                    // Create a new Tinkerforge BrickletAmbientLight object with data from the database
                    brickletAmbientLight = new BrickletAmbientLight(illuminanceSensorDomain.getUid(), ipConnection);

                    brickletAmbientLight.setIlluminanceCallbackPeriod(5000);

                    brickletAmbientLight.addIlluminanceListener(new IlluminanceListener(illuminanceSensorDomain,
                            LEDStripService, LEDStripRepository));
                } else {
                    LOG.error("Error setting up illuminance sensor {}: Brick {} not available.",
                        illuminanceSensorDomain.getName(), illuminanceSensorDomain.getBrickDomain().getHostname());

                    brickletAmbientLight = null;
                }
            } catch (TimeoutException | NotConnectedException e) {
                LOG.error("Error setting up illuminance sensor {}: {}", illuminanceSensorDomain.getName(),
                    e.toString());
                brickletAmbientLight = null; // if there is an error, we don't want to use this
            }

            if (brickletAmbientLight != null) {
                // add it to the HashMap
                illuminanceSensors.put(illuminanceSensorDomain, brickletAmbientLight);
            }
        }

        LOG.debug("Finished setting up sensor {}.", illuminanceSensorDomain.getName());

        return illuminanceSensors.get(illuminanceSensorDomain); // retrieve and return
    }


    /**
     * Clear the registry.
     */
    @Override
    public void clear() {

        illuminanceSensors.clear();
    }
}
