package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;


/**
 * BrickletLEDStripWrapper.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickletLEDStripWrapper extends BrickletLEDStrip {

    private final IPConnection ipConnection;

    /**
     * Creates an object with the unique device ID \c uid. and adds it to the IP Connection \c ipcon.
     *
     * @param  uid  the uid
     * @param  ipcon  the ipcon
     */
    public BrickletLEDStripWrapper(String uid, IPConnection ipcon) {

        super(uid, ipcon);
        ipConnection = ipcon;
    }

    /**
     * Disconnect the bricklet and its brick.
     */
    public void disconnect() {

        try {
            ipConnection.disconnect();
        } catch (NotConnectedException exception) {
            throw new LEDStripConnectionException("Error disconnecting:", exception);
        }
    }
}
