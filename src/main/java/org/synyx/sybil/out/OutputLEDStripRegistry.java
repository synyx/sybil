package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.common.IPConnectionRegistry;
import org.synyx.sybil.database.OutputLEDStripRepository;
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
public class OutputLEDStripRegistry {

    private Map<String, OutputLEDStrip> ledStrips = new HashMap<>();
    private OutputLEDStripRepository outputLEDStripRepository;
    private IPConnectionRegistry ipConnectionRegistry;

    // Constructor, called when Spring autowires it somewhere. Dependencies are injected.
    @Autowired
    public OutputLEDStripRegistry(OutputLEDStripRepository outputLEDStripRepository,
        IPConnectionRegistry ipConnectionRegistry) {

        this.outputLEDStripRepository = outputLEDStripRepository;
        this.ipConnectionRegistry = ipConnectionRegistry;
    }

    /**
     * Get a LED strip object, instantiate a new one if necessary.
     *
     * @param  Identifier  The LED strip's Identifier as configured in the database.
     *
     * @return  The LED strip object.
     */
    public OutputLEDStrip get(String Identifier) {

        if (!ledStrips.containsKey(Identifier)) { // if there is no LED Strip with that id in the HashMap yet...

            BrickletLEDStrip brickletLEDStrip = null; // since there is a try, it might end up undefined

            // fetch the data from the database
            OutputLEDStripDomain outputLEDStripDomain = outputLEDStripRepository.findByName(Identifier);

            try {
                // Create a new Tinkerforge brickletLEDStrip object with data from the database
                brickletLEDStrip = new BrickletLEDStrip(outputLEDStripDomain.getUid(),
                        ipConnectionRegistry.get(outputLEDStripDomain.getHostname(), outputLEDStripDomain.getPort()));
                brickletLEDStrip.setFrameDuration(10); // Always go for the minimum (i.e. fastest) frame duration
                brickletLEDStrip.setChipType(2812); // We only use 2812 chips
            } catch (TimeoutException | NotConnectedException | IOException | AlreadyConnectedException e) {
                e.printStackTrace();
            }

            // get a new OutputLEDStrip object
            OutputLEDStrip outputLedStrip = new OutputLEDStrip(brickletLEDStrip, outputLEDStripDomain.getLength(),
                    Identifier);
            ledStrips.put(Identifier, outputLedStrip); // add it to the HashMap
        }

        return ledStrips.get(Identifier); // retrieve and return
    }
}
