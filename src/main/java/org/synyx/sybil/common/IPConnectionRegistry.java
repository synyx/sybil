package org.synyx.sybil.common;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


/**
 * IPConnectionRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IPConnectionRegistry {

    private static Map<String, IPConnection> ipConnections = new HashMap<>();

    /**
     * Get an IPConnection object, instantiate a new one if necessary.
     *
     * @param  hostname  The hostname to connect to.
     * @param  port  The port to connect to (optional, defaults to 4223).
     *
     * @return  The IPConnection object.
     *
     * @throws  AlreadyConnectedException  A connection already exists to this host.
     * @throws  IOException  IO Error.
     */
    public static IPConnection get(String hostname, int port) throws AlreadyConnectedException, IOException {

        if (!ipConnections.containsKey(hostname)) {
            IPConnection ipConnection = new IPConnection();
            ipConnection.connect(hostname, port);
            ipConnections.put(hostname, ipConnection);
        }

        return ipConnections.get(hostname);
    }


    /**
     * Get an IPConnection object, instantiate a new one if necessary.
     *
     * @param  hostname  The hostname to connect to.
     *
     * @return  The IPConnection object
     *
     * @throws  AlreadyConnectedException  A connection already exists to this host.
     * @throws  IOException  IO Error.
     */
    public static IPConnection get(String hostname) throws IOException, AlreadyConnectedException {

        return get(hostname, 4223);
    }
}
