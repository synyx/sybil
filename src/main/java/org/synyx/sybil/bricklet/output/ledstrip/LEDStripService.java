package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
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
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * LEDStripService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class LEDStripService implements BrickletService {

    private static final Logger LOG = LoggerFactory.getLogger(LEDStripService.class);
    private static final int FRAME_DURATION = 10;
    private static final int CHIP_TYPE = 2812;

    private Map<LEDStripDomain, LEDStrip> outputLEDStrips = new HashMap<>();
    private BrickService brickService;
    private LEDStripRepository ledStripRepository;
    private GraphDatabaseService graphDatabaseService;

    /**
     * Instantiates a new LEDStrip registry.
     *
     * @param  brickService  The brick registry
     * @param  ledStripRepository  LED strip database repository
     * @param  graphDatabaseService  Neo4j service
     */
    @Autowired
    public LEDStripService(BrickService brickService, LEDStripRepository ledStripRepository,
        GraphDatabaseService graphDatabaseService) {

        this.brickService = brickService;
        this.ledStripRepository = ledStripRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    /**
     * Gets domain.
     *
     * @param  name  the name
     *
     * @return  the domain
     */
    public LEDStripDomain getDomain(String name) {

        return ledStripRepository.findByName(name);
    }


    /**
     * Save domain.
     *
     * @param  ledStripDomain  the ledStrip domain
     *
     * @return  the illuminance sensor domain
     */
    public LEDStripDomain saveDomain(LEDStripDomain ledStripDomain) {

        return ledStripRepository.save(ledStripDomain);
    }


    /**
     * Gets all domains.
     *
     * @return  the all domains
     */
    public List<LEDStripDomain> getAllDomains() {

        List<LEDStripDomain> ledStripDomains;

        try(Transaction tx = graphDatabaseService.beginTx()) {
            ledStripDomains = new ArrayList<>(IteratorUtil.asCollection(ledStripRepository.findAll()));

            tx.success();
        }

        return ledStripDomains;
    }


    /**
     * Delete all domains.
     */
    public void deleteAllDomains() {

        ledStripRepository.deleteAll();
    }


    /**
     * Get a LEDStrip object, instantiate a new one if necessary.
     *
     * @param  ledStripDomain  The bricklet's domain from the database.
     *
     * @return  The actual LEDStrip object.
     */
    public LEDStrip getLEDStrip(LEDStripDomain ledStripDomain) {

        if (ledStripDomain == null) {
            return null;
        }

        if (!outputLEDStrips.containsKey(ledStripDomain)) {
            BrickletLEDStrip brickletLEDStrip;

            try {
                IPConnection ipConnection = brickService.getIPConnection(ledStripDomain.getBrickDomain(), this);

                if (ipConnection != null) {
                    brickletLEDStrip = new BrickletLEDStrip(ledStripDomain.getUid(), ipConnection);
                    brickletLEDStrip.setFrameDuration(FRAME_DURATION);
                    brickletLEDStrip.setChipType(CHIP_TYPE);
                } else {
                    LOG.error("Error setting up LED Strip {}: Brick {} not available.", ledStripDomain.getName(),
                        ledStripDomain.getBrickDomain().getHostname());

                    brickletLEDStrip = null;
                }
            } catch (TimeoutException | NotConnectedException e) {
                LOG.error("Error setting up LED Strip {}: {}", ledStripDomain.getName(), e.toString());
                brickletLEDStrip = null;
            }

            if (brickletLEDStrip != null) {
                LEDStrip ledStrip = new LEDStrip(brickletLEDStrip, ledStripDomain.getLength(),
                        ledStripDomain.getName());

                outputLEDStrips.put(ledStripDomain, ledStrip);
            }
        }

        return outputLEDStrips.get(ledStripDomain);
    }


    /**
     * Remove all OutputLEDStrips from the registry.
     */
    @Override
    public void clear() {

        outputLEDStrips.clear();
    }
}
