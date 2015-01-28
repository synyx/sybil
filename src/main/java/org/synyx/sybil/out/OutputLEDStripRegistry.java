package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.common.BrickRegistry;
import org.synyx.sybil.common.BrickletRegistry;
import org.synyx.sybil.domain.OutputLEDStripDomain;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


/**
 * OutputLEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class OutputLEDStripRegistry implements BrickletRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(OutputLEDStripRegistry.class);

    private Map<OutputLEDStripDomain, OutputLEDStrip> outputLEDStrips = new HashMap<>();
    private BrickRegistry brickRegistry;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.
    /**
     * Instantiates a new OutputLEDStrip registry.
     *
     * @param  brickRegistry  The brick registry
     */
    @Autowired
    public OutputLEDStripRegistry(BrickRegistry brickRegistry) {

        this.brickRegistry = brickRegistry;
    }

    /**
     * Get a OutputLEDStrip object, instantiate a new one if necessary.
     *
     * @param  outputLEDStripDomain  The bricklet's domain from the database.
     *
     * @return  The actual OutputLEDStrip object.
     */
    public OutputLEDStrip get(OutputLEDStripDomain outputLEDStripDomain) {

        // if there is no LED Strip with that id in the HashMap yet...
        if (!outputLEDStrips.containsKey(outputLEDStripDomain)) {
            BrickletLEDStrip brickletLEDStrip = null; // since there is a try, it might end up undefined

            try {
                // get the connectin to the Brick, passing the BrickDomain and the calling object
                IPConnection ipConnection = brickRegistry.get(outputLEDStripDomain.getBrickDomain(), this);

                // Create a new Tinkerforge brickletLEDStrip object with data from the database
                brickletLEDStrip = new BrickletLEDStrip(outputLEDStripDomain.getUid(), ipConnection);
                brickletLEDStrip.setFrameDuration(10); // Always go for the minimum (i.e. fastest) frame duration
                brickletLEDStrip.setChipType(2812); // We only use 2812 chips
            } catch (TimeoutException | NotConnectedException | IOException | AlreadyConnectedException e) {
                LOG.error(e.toString());
            }

            if (brickletLEDStrip != null) {
                // get a new OutputLEDStrip object
                OutputLEDStrip outputLedStrip = new OutputLEDStrip(brickletLEDStrip, outputLEDStripDomain.getLength(),
                        outputLEDStripDomain.getName());

                // add it to the HashMap
                outputLEDStrips.put(outputLEDStripDomain, outputLedStrip);
            }
        }

        return outputLEDStrips.get(outputLEDStripDomain); // retrieve and return
    }


    /**
     * Remove all OutputLEDStrips from the registry.
     */
    @Override
    public void clear() {

        outputLEDStrips.clear();
    }
}
