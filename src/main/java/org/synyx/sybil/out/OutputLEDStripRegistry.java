package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.synyx.sybil.common.IPConnectionRegistry;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


/**
 * OutputLEDStripRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class OutputLEDStripRegistry {

    private static Map<String, OutputLEDStrip> ledStrips = new HashMap<>();

    /**
     * Get a LED strip object, instantiate a new one if necessary.
     *
     * @param  Identifier  The LED strip's Identifier as configured in the database.
     *
     * @return  The LED strip object.
     */
    public static OutputLEDStrip get(String Identifier) {

        if (!ledStrips.containsKey(Identifier)) {
            BrickletLEDStrip brickletLEDStrip = null;

            try {
                brickletLEDStrip = new BrickletLEDStrip("p3c", IPConnectionRegistry.get("localhost"));
                brickletLEDStrip.setFrameDuration(10);
                brickletLEDStrip.setChipType(2812);
            } catch (TimeoutException | NotConnectedException | IOException | AlreadyConnectedException e) {
                e.printStackTrace();
            }

            OutputLEDStrip outputLedStrip = new OutputLEDStrip(brickletLEDStrip, 30, Identifier);
            ledStrips.put(Identifier, outputLedStrip);
        }

        return ledStrips.get(Identifier);
    }
}
