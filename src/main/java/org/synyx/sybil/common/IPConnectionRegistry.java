package org.synyx.sybil.common;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;

import org.springframework.stereotype.Service;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


/**
 * IPConnectionRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class IPConnectionRegistry {

    private Map<String, IPConnection> ipConnections = new HashMap<>();

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
    public IPConnection get(String hostname, int port) throws AlreadyConnectedException, IOException {

        if (!ipConnections.containsKey(hostname)) { // if it isn't in the Map yet...

            IPConnection ipConnection = new IPConnection(); // ... make a new one...
            ipConnection.connect(hostname, port); // ... connect it ...
            ipConnections.put(hostname, ipConnection); // ... and add it to the map.
        }

        return ipConnections.get(hostname); // retrieve and return
    }
}
