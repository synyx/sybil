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

@Service
public class SingleStatusOnLEDStripRegistry implements BrickletService {

    private Map<LEDStrip, SingleStatusOnLEDStrip> singleStatusOnLEDStrips = new HashMap<>();

    /**
     * Get a SingleStatusOnLEDStrip object, instantiate a new one if necessary.
     *
     * @param  ledStrip  The LED Strip object.
     *
     * @return  The SingleStatusOnLEDStrip object.
     */
    public SingleStatusOnLEDStrip get(LEDStrip ledStrip) {

        if (ledStrip == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!singleStatusOnLEDStrips.containsKey(ledStrip)) {
            // add it to the HashMap
            singleStatusOnLEDStrips.put(ledStrip, new SingleStatusOnLEDStrip(ledStrip));
        }

        return singleStatusOnLEDStrips.get(ledStrip);
    }


    /**
     * Get single status on lED strip with custom status colors.
     *
     * @param  ledStrip  The LED Strip object.
     * @param  okay  The Color for status OKAY
     * @param  warning  The Color for status WARNING
     * @param  critical  The Color for status CRITICAL
     *
     * @return  The SingleStatusOnLEDStrip object.
     */
    public SingleStatusOnLEDStrip get(LEDStrip ledStrip, Color okay, Color warning, Color critical) {

        if (ledStrip == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!singleStatusOnLEDStrips.containsKey(ledStrip)) {
            // add it to the HashMap
            singleStatusOnLEDStrips.put(ledStrip, new SingleStatusOnLEDStrip(ledStrip, okay, warning, critical));
        }

        return singleStatusOnLEDStrips.get(ledStrip);
    }


    /**
     * Remove all SingleStatusOnLEDStrips from the registry.
     */
    @Override
    public void clear() {

        singleStatusOnLEDStrips.clear();
    }
}
