package org.synyx.sybil.bricklet.output.relay;

import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.BrickletRegistry;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;

import java.util.HashMap;
import java.util.Map;


/**
 * Relay registry. Serves OutputRelays, instantiating new ones if necessary.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class RelayRegistry implements BrickletRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(RelayRegistry.class);

    private Map<RelayDomain, Relay> outputRelayMap = new HashMap<>();
    private BrickService brickService;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.
    /**
     * Instantiates a new Relay registry.
     *
     * @param  brickService  The brick registry
     */
    @Autowired
    public RelayRegistry(BrickService brickService) {

        this.brickService = brickService;
    }

    /**
     * Get a Relay object, instantiate a new one if necessary.
     *
     * @param  relayDomain  The bricklet's domain from the database.
     *
     * @return  The actual Relay object.
     */
    public Relay get(RelayDomain relayDomain) {

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
