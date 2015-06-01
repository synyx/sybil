package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickRegistry;
import org.synyx.sybil.bricklet.BrickletRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;

import java.util.HashMap;
import java.util.Map;


/**
 * LEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class LEDStripRegistry implements BrickletRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(LEDStripRegistry.class);
    private static final int FRAME_DURATION = 10;
    private static final int CHIP_TYPE = 2812;

    private Map<LEDStripDomain, LEDStrip> outputLEDStrips = new HashMap<>();
    private BrickRegistry brickRegistry;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.
    /**
     * Instantiates a new LEDStrip registry.
     *
     * @param  brickRegistry  The brick registry
     */
    @Autowired
    public LEDStripRegistry(BrickRegistry brickRegistry) {

        this.brickRegistry = brickRegistry;
    }

    /**
     * Get a LEDStrip object, instantiate a new one if necessary.
     *
     * @param  LEDStripDomain  The bricklet's domain from the database.
     *
     * @return  The actual LEDStrip object.
     */
    public LEDStrip get(LEDStripDomain LEDStripDomain) {

        if (LEDStripDomain == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!outputLEDStrips.containsKey(LEDStripDomain)) {
            BrickletLEDStrip brickletLEDStrip; // since there is a try, it might end up undefined

            try {
                // get the connecting to the Brick, passing the BrickDomain and the calling object
                IPConnection ipConnection = brickRegistry.get(LEDStripDomain.getBrickDomain(), this);

                if (ipConnection != null) {
                    // Create a new Tinkerforge brickletLEDStrip object with data from the database
                    brickletLEDStrip = new BrickletLEDStrip(LEDStripDomain.getUid(), ipConnection);
                    brickletLEDStrip.setFrameDuration(FRAME_DURATION); // Always go for the minimum (i.e. fastest) frame duration
                    brickletLEDStrip.setChipType(CHIP_TYPE); // We only use 2812 chips
                } else {
                    LOG.error("Error setting up LED Strip {}: Brick {} not available.", LEDStripDomain.getName(),
                        LEDStripDomain.getBrickDomain().getHostname());

                    brickletLEDStrip = null;
                }
            } catch (TimeoutException | NotConnectedException e) {
                LOG.error("Error setting up LED Strip {}: {}", LEDStripDomain.getName(), e.toString());
                brickletLEDStrip = null; // if there is an error, we don't want to use this
            }

            if (brickletLEDStrip != null) {
                // get a new LEDStrip object
                LEDStrip ledStrip = new LEDStrip(brickletLEDStrip, LEDStripDomain.getLength(),
                        LEDStripDomain.getName());

                // add it to the HashMap
                outputLEDStrips.put(LEDStripDomain, ledStrip);
            }
        }

        return outputLEDStrips.get(LEDStripDomain); // retrieve and return
    }


    /**
     * Remove all OutputLEDStrips from the registry.
     */
    @Override
    public void clear() {

        outputLEDStrips.clear();
    }
}
