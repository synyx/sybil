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

import org.synyx.sybil.brick.OldBrickService;
import org.synyx.sybil.bricklet.BrickletService;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorRepository;
import org.synyx.sybil.bricklet.output.ledstrip.OldLEDStripService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * LEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class IlluminanceService implements BrickletService {

    private static final Logger LOG = LoggerFactory.getLogger(IlluminanceService.class);
    private static final int CALLBACK_PERIOD = 5000;

    private Map<IlluminanceSensorDomain, BrickletAmbientLight> illuminanceSensors = new HashMap<>();
    private OldBrickService brickService;
    private OldLEDStripService ledStripService;
    private IlluminanceSensorRepository illuminanceSensorRepository;
    private GraphDatabaseService graphDatabaseService;

    /**
     * Instantiates a new Illuminance sensor registry.
     *
     * @param  brickService  the brick registry
     * @param  ledStripService  the output lED strip registry
     * @param  illuminanceSensorRepository  the illuminance sensor repository
     * @param  graphDatabaseService  the graph database service
     */
    @Autowired
    public IlluminanceService(OldBrickService brickService, OldLEDStripService ledStripService,
        IlluminanceSensorRepository illuminanceSensorRepository, GraphDatabaseService graphDatabaseService) {

        this.brickService = brickService;
        this.ledStripService = ledStripService;
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

        try(Transaction tx = graphDatabaseService.beginTx()) {
            illuminanceSensorDomains = new ArrayList<>(IteratorUtil.asCollection(
                        illuminanceSensorRepository.findAll()));

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
            setupIlluminanceSensor(illuminanceSensorDomain);
        }

        LOG.debug("Finished setting up sensor {}.", illuminanceSensorDomain.getName());

        return illuminanceSensors.get(illuminanceSensorDomain);
    }


    private void setupIlluminanceSensor(IlluminanceSensorDomain illuminanceSensorDomain) {

        BrickletAmbientLight brickletAmbientLight;

        try {
            IPConnection ipConnection = brickService.getIPConnection(illuminanceSensorDomain.getBrickDomain(), this);

            if (ipConnection == null) {
                LOG.error("Error setting up illuminance sensor {}: Brick {} not available.",
                    illuminanceSensorDomain.getName(), illuminanceSensorDomain.getBrickDomain().getHostname());

                brickletAmbientLight = null;
            } else {
                brickletAmbientLight = new BrickletAmbientLight(illuminanceSensorDomain.getUid(), ipConnection);

                brickletAmbientLight.setIlluminanceCallbackPeriod(CALLBACK_PERIOD);

                brickletAmbientLight.addIlluminanceListener(new IlluminanceListener(illuminanceSensorDomain,
                        ledStripService));
            }
        } catch (TimeoutException | NotConnectedException e) {
            LOG.error("Error setting up illuminance sensor {}: {}", illuminanceSensorDomain.getName(), e.toString());
            brickletAmbientLight = null;
        }

        if (brickletAmbientLight != null) {
            illuminanceSensors.put(illuminanceSensorDomain, brickletAmbientLight);
        }
    }


    /**
     * Clear the registry.
     */
    @Override
    public void clear() {

        illuminanceSensors.clear();
    }
}
