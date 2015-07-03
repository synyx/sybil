package org.synyx.sybil.brick;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ConnectionListener.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickConnectionListener implements IPConnection.ConnectedListener {

    private static final Logger LOG = LoggerFactory.getLogger(BrickConnectionListener.class);

    private IPConnection ipConnection = null;

    public BrickConnectionListener(IPConnection ipConnection) {

        this.ipConnection = ipConnection;
    }

    @Override
    public void connected(short connectReason) { // NOSONAR Tinkerforge library uses shorts

        try {
            ipConnection.enumerate();
        } catch (NotConnectedException e) {
            LOG.error("Cannot enumerate: Not connected.");
        }
    }
}
