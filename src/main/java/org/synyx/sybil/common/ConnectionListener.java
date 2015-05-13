package org.synyx.sybil.common;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ConnectionListener.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class ConnectionListener implements IPConnection.ConnectedListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionListener.class);

    private IPConnection ipConnection = null;

    public ConnectionListener(IPConnection ipConnection) {

        this.ipConnection = ipConnection;
    }

    @Override
    public void connected(short connectReason) {

        try {
            ipConnection.enumerate();
        } catch (NotConnectedException e) {
            LOG.error("Cannot enumerate: Not connected.");
        }
    }
}
