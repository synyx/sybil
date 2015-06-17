package org.synyx.sybil.bricklet.output.relay;

import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.BrickletService;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;
import org.synyx.sybil.bricklet.output.relay.database.RelayRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Relay registry. Serves OutputRelays, instantiating new ones if necessary.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class RelayService implements BrickletService {

    private static final Logger LOG = LoggerFactory.getLogger(RelayService.class);

    private Map<RelayDomain, Relay> outputRelayMap = new HashMap<>();
    private BrickService brickService;
    private RelayRepository relayRepository;
    private GraphDatabaseService graphDatabaseService;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.
    /**
     * Instantiates a new Relay registry.
     *
     * @param  brickService  The brick registry
     * @param  relayRepository  Relay database
     * @param  graphDatabaseService  Neo4j service
     */
    @Autowired
    public RelayService(BrickService brickService, RelayRepository relayRepository,
        GraphDatabaseService graphDatabaseService) {

        this.brickService = brickService;
        this.relayRepository = relayRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    /**
     * Gets domain.
     *
     * @param  name  the name
     *
     * @return  the domain
     */
    public RelayDomain getDomain(String name) {

        return relayRepository.findByName(name);
    }


    /**
     * Save domain.
     *
     * @param  relayDomain  the relay domain
     *
     * @return  the illuminance sensor domain
     */
    public RelayDomain saveDomain(RelayDomain relayDomain) {

        return relayRepository.save(relayDomain);
    }


    /**
     * Gets all domains.
     *
     * @return  the all domains
     */
    public List<RelayDomain> getAllDomains() {

        List<RelayDomain> relayDomains;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all sensors from database and cast them into a list so that they're actually fetched
            relayDomains = new ArrayList<>(IteratorUtil.asCollection(relayRepository.findAll()));

            // end transaction
            tx.success();
        }

        return relayDomains;
    }


    /**
     * Delete all domains.
     */
    public void deleteAllDomains() {

        relayRepository.deleteAll();
    }


    /**
     * Get a Relay object, instantiate a new one if necessary.
     *
     * @param  relayDomain  The bricklet's domain from the database.
     *
     * @return  The actual Relay object.
     */
    public Relay getRelay(RelayDomain relayDomain) {

        if (relayDomain == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!outputRelayMap.containsKey(relayDomain)) {
            BrickletDualRelay brickletDualRelay; // since there is a try, it might end up undefined

            // get the connecting to the Brick, passing the BrickDomain and the calling object
            IPConnection ipConnection = brickService.getIPConnection(relayDomain.getBrickDomain(), this);

            if (ipConnection != null) {
                // Create a new Tinkerforge brickletDualRelay object with data from the database
                brickletDualRelay = new BrickletDualRelay(relayDomain.getUid(), ipConnection);
            } else {
                LOG.warn("Error setting up relay {}: Brick {} not available.", relayDomain.getName(),
                    relayDomain.getBrickDomain().getHostname());

                brickletDualRelay = null;
            }

            if (brickletDualRelay != null) {
                // get a new Relay object
                Relay relay = new Relay(brickletDualRelay, relayDomain.getName());

                // add it to the HashMap
                outputRelayMap.put(relayDomain, relay);
            }
        }

        return outputRelayMap.get(relayDomain); // retrieve and return
    }


    /**
     * Clear the registry.
     */
    @Override
    public void clear() {

        outputRelayMap.clear();
    }
}
