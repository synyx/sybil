package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * IPConnectionFactory.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IPConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(IPConnectionFactory.class);

    public IPConnection createIPConnection(String hostname, int port) throws AlreadyConnectedException, IOException {

        LOG.info("Creating IPConnection to {}:{}", hostname, port);

        IPConnection ipConnection = new IPConnection();
        ipConnection.connect(hostname, port);

        LOG.info("Successfully connected to {}:{}", hostname, port);

        return ipConnection;
    }
}
