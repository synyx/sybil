package org.synyx.sybil.common;

import com.tinkerforge.AlreadyConnectedException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.domain.BrickletDomain;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


/**
 * OutputLEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class BrickletRegistry {

    private Map<BrickletDomain, Bricklet> bricklets = new HashMap<>();
    private BrickRegistry brickRegistry;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.
    /**
     * Instantiates a new Output lED strip registry.
     *
     * @param  brickRegistry  The brick registry
     */
    @Autowired
    public BrickletRegistry(BrickRegistry brickRegistry) {

        this.brickRegistry = brickRegistry;
    }

    /**
     * Get a bricklet object, instantiate a new one if necessary.
     *
     * @param  brickletDomain  The bricklet's domain from the database.
     *
     * @return  The actual bricklet object.
     */
    public Bricklet get(BrickletDomain brickletDomain) {

        if (!bricklets.containsKey(brickletDomain)) { // if there is no LED Strip with that id in the HashMap yet...

            try {
                brickRegistry.register(brickletDomain.getBrickDomain(), this);
            } catch (AlreadyConnectedException | IOException e) {
                e.printStackTrace();
            }
        }

        return bricklets.get(brickletDomain); // retrieve and return
    }


    /**
     * Put a new bricklet into the Registry.
     *
     * @param  brickletDomain  The bricklet's domain from the database..
     * @param  bricklet  The actual brciklet object.
     */
    public void put(BrickletDomain brickletDomain, Bricklet bricklet) {

        bricklets.put(brickletDomain, bricklet);
    }
}
