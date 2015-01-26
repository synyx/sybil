package org.synyx.sybil.common;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.stereotype.Service;

import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.BrickletDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.out.OutputLEDStrip;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


/**
 * IPConnectionRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class BrickRegistry {

    private Map<BrickDomain, IPConnection> ipConnections = new HashMap<>();

    /**
     * Register a connection to a Tinkerforge Brick.
     *
     * @param  brickDomain  the Brick's domain object.
     *
     * @throws  AlreadyConnectedException  A connection already exists to this host.
     * @throws  AlreadyConnectedException  A connection already exists to this host.
     */
    public void register(BrickDomain brickDomain, BrickletRegistry brickletRegistry) throws AlreadyConnectedException,
        IOException {

        if (!ipConnections.containsKey(brickDomain)) { // if it isn't in the Map yet...

            IPConnection ipConnection = new IPConnection(); // ... make a new one...

            // add a callback method for when it connects, so this is reconnect-safe
            ipConnection.addConnectedListener(new IPConnection.ConnectedListener() {

                    @Override
                    public void connected(short connectReason) {

                        // get all the connected bricklets and iterate over them
                        for (BrickletDomain brickletDomain : brickDomain.getBricklets()) {
                            // switch based on bricklet type
                            switch (brickletDomain.getType()) {
                                case "OutputLEDStrip":

                                    // cast generic Interface into specific implementation
                                    OutputLEDStripDomain outputLEDStripDomain = (OutputLEDStripDomain) brickletDomain;

                                    // create a new Tinkerforge Bricklet object
                                    BrickletLEDStrip brickletLEDStrip = new BrickletLEDStrip(
                                            outputLEDStripDomain.getUid(), ipConnection);
                                    try {
                                        brickletLEDStrip.setChipType(2812); // only chiptype in use
                                        brickletLEDStrip.setFrameDuration(10); // set for highest possible framerate
                                    } catch (TimeoutException | NotConnectedException e) {
                                        e.printStackTrace();
                                    }

                                    // create a new OutputLEDStrip object
                                    OutputLEDStrip outputLEDStrip = new OutputLEDStrip(brickletLEDStrip,
                                            outputLEDStripDomain.getLength(), outputLEDStripDomain.getName());

                                    // add to brickletRegistry
                                    brickletRegistry.put(brickletDomain, outputLEDStrip);
                                    break;

                                default: // unknown bricklet

                                    // TODO: WTF?
                                    break;
                            }
                        }
                    }
                });

            ipConnection.connect(brickDomain.getHostname(), brickDomain.getPort()); // ... connect it ...
            ipConnections.put(brickDomain, ipConnection); // ... and add it to the map.
        }
    }
}
