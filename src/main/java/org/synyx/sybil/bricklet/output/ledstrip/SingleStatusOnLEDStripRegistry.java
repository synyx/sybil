package org.synyx.sybil.bricklet.output.ledstrip;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.BrickletRegistry;

import java.util.HashMap;
import java.util.Map;


/**
 * OutputLEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class SingleStatusOnLEDStripRegistry implements BrickletRegistry {

    private Map<OutputLEDStrip, SingleStatusOnLEDStrip> singleStatusOnLEDStrips = new HashMap<>();

    /**
     * Get a SingleStatusOnLEDStrip object, instantiate a new one if necessary.
     *
     * @param  outputLEDStrip  The LED Strip object.
     *
     * @return  The SingleStatusOnLEDStrip object.
     */
    public SingleStatusOnLEDStrip get(OutputLEDStrip outputLEDStrip) {

        if (outputLEDStrip == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!singleStatusOnLEDStrips.containsKey(outputLEDStrip)) {
            // add it to the HashMap
            singleStatusOnLEDStrips.put(outputLEDStrip, new SingleStatusOnLEDStrip(outputLEDStrip));
        }

        return singleStatusOnLEDStrips.get(outputLEDStrip); // retrieve and return
    }


    /**
     * Get single status on lED strip with custom status colors.
     *
     * @param  outputLEDStrip  The LED Strip object.
     * @param  okay  The Color for status OKAY
     * @param  warning  The Color for status WARNING
     * @param  critical  The Color for status CRITICAL
     *
     * @return  The SingleStatusOnLEDStrip object.
     */
    public SingleStatusOnLEDStrip get(OutputLEDStrip outputLEDStrip, Color okay, Color warning, Color critical) {

        if (outputLEDStrip == null) {
            return null;
        }

        // if there is no LED Strip with that id in the HashMap yet...
        if (!singleStatusOnLEDStrips.containsKey(outputLEDStrip)) {
            // add it to the HashMap
            singleStatusOnLEDStrips.put(outputLEDStrip,
                new SingleStatusOnLEDStrip(outputLEDStrip, okay, warning, critical));
        }

        return singleStatusOnLEDStrips.get(outputLEDStrip); // retrieve and return
    }


    /**
     * Remove all SingleStatusOnLEDStrips from the registry.
     */
    @Override
    public void clear() {

        singleStatusOnLEDStrips.clear();
    }
}
