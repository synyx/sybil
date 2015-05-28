package org.synyx.sybil.out;

import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickRegistry;
import org.synyx.sybil.common.BrickletRegistry;
import org.synyx.sybil.domain.OutputRelayDomain;

import java.util.HashMap;
import java.util.Map;


/**
 * OutputRelay registry. Serves OutputRelays, instantiating new ones if necessary.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class OutputRelayRegistry implements BrickletRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(OutputRelayRegistry.class);

    private Map<OutputRelayDomain, OutputRelay> outputRelayMap = new HashMap<>();
    private BrickRegistry brickRegistry;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.
    /**
     * Instantiates a new OutputRelay registry.
     *
     * @param  brickRegistry  The brick registry
     */
    @Autowired
    public OutputRelayRegistry(BrickRegistry brickRegistry) {

        this.brickRegistry = brickRegistry;
    }

    /**
     * Get a OutputRelay object, instantiate a new one if necessary.
     *
     * @param  outputRelayDomain  The bricklet's domain from the database.
     *
     * @return  The actual OutputRelay object.
     */
    public OutputRelay get(OutputRelayDomain outputRelayDomain) {

        if (outputRelayDomain == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!outputRelayMap.containsKey(outputRelayDomain)) {
            BrickletDualRelay brickletDualRelay; // since there is a try, it might end up undefined

            // get the connecting to the Brick, passing the BrickDomain and the calling object
            IPConnection ipConnection = brickRegistry.get(outputRelayDomain.getBrickDomain(), this);

            if (ipConnection != null) {
                // Create a new Tinkerforge brickletDualRelay object with data from the database
                brickletDualRelay = new BrickletDualRelay(outputRelayDomain.getUid(), ipConnection);
            } else {
                LOG.warn("Error setting up relay {}: Brick {} not available.", outputRelayDomain.getName(),
                    outputRelayDomain.getBrickDomain().getHostname());

                brickletDualRelay = null;
            }

            if (brickletDualRelay != null) {
                // get a new OutputRelay object
                OutputRelay outputRelay = new OutputRelay(brickletDualRelay, outputRelayDomain.getName());

                // add it to the HashMap
                outputRelayMap.put(outputRelayDomain, outputRelay);
            }
        }

        return outputRelayMap.get(outputRelayDomain); // retrieve and return
    }


    /**
     * Clear the registry.
     */
    @Override
    public void clear() {

        outputRelayMap.clear();
    }
}
