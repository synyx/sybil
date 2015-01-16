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

@Service
public class OutputLEDStripRegistry {

    private Map<String, OutputLEDStrip> ledStrips = new HashMap<>();
    private OutputLEDStripRepository outputLEDStripRepository;
    private IPConnectionRegistry ipConnectionRegistry;

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

        if (!ledStrips.containsKey(Identifier)) {
            BrickletLEDStrip brickletLEDStrip = null;

            OutputLEDStripDomain outputLEDStripDomain = outputLEDStripRepository.findByName(Identifier);

            try {
                brickletLEDStrip = new BrickletLEDStrip(outputLEDStripDomain.getUid(),
                        ipConnectionRegistry.get(outputLEDStripDomain.getHostname(), outputLEDStripDomain.getPort()));
                brickletLEDStrip.setFrameDuration(10);
                brickletLEDStrip.setChipType(2812);
            } catch (TimeoutException | NotConnectedException | IOException | AlreadyConnectedException e) {
                e.printStackTrace();
            }

            OutputLEDStrip outputLedStrip = new OutputLEDStrip(brickletLEDStrip, outputLEDStripDomain.getLength(),
                    Identifier);
            ledStrips.put(Identifier, outputLedStrip);
        }

        return ledStrips.get(Identifier);
    }
}
