package org.synyx.sybil.bricklet.output.ledstrip;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.BrickletService;

import java.util.HashMap;
import java.util.Map;


/**
 * LEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class SingleStatusOnLEDStripRegistry implements BrickletService {

    private Map<LEDStrip, SingleStatusOnLEDStrip> singleStatusOnLEDStrips = new HashMap<>();

    /**
     * Get a SingleStatusOnLEDStrip object, instantiate a new one if necessary.
     *
     * @param  LEDStrip  The LED Strip object.
     *
     * @return  The SingleStatusOnLEDStrip object.
     */
    public SingleStatusOnLEDStrip get(LEDStrip LEDStrip) {

        if (LEDStrip == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!singleStatusOnLEDStrips.containsKey(LEDStrip)) {
            // add it to the HashMap
            singleStatusOnLEDStrips.put(LEDStrip, new SingleStatusOnLEDStrip(LEDStrip));
        }

        return singleStatusOnLEDStrips.get(LEDStrip); // retrieve and return
    }


    /**
     * Get single status on lED strip with custom status colors.
     *
     * @param  LEDStrip  The LED Strip object.
     * @param  okay  The Color for status OKAY
     * @param  warning  The Color for status WARNING
     * @param  critical  The Color for status CRITICAL
     *
     * @return  The SingleStatusOnLEDStrip object.
     */
    public SingleStatusOnLEDStrip get(LEDStrip LEDStrip, Color okay, Color warning, Color critical) {

        if (LEDStrip == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!singleStatusOnLEDStrips.containsKey(LEDStrip)) {
            // add it to the HashMap
            singleStatusOnLEDStrips.put(LEDStrip, new SingleStatusOnLEDStrip(LEDStrip, okay, warning, critical));
        }

        return singleStatusOnLEDStrips.get(LEDStrip); // retrieve and return
    }


    /**
     * Remove all SingleStatusOnLEDStrips from the registry.
     */
    @Override
    public void clear() {

        singleStatusOnLEDStrips.clear();
    }
}
