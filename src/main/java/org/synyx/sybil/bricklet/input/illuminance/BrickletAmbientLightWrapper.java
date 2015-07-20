package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;


/**
 * BrickletAmbientLightWrapper.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickletAmbientLightWrapper extends BrickletAmbientLight {

    private final IPConnection ipConnection;

    /**
     * Creates an object with the unique device ID \c uid. and adds it to the IP Connection \c ipcon.
     *
     * @param  uid  the uid
     * @param  ipcon  the ipcon
     */
    public BrickletAmbientLightWrapper(String uid, IPConnection ipcon) {

        super(uid, ipcon);
        ipConnection = ipcon;
    }

    /**
     * Disconnect the bricklet and its brick.
     *
     * @throws  com.tinkerforge.NotConnectedException  the not connected exception
     */
    public void disconnect() throws NotConnectedException {

        ipConnection.disconnect();
    }
}
