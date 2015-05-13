package org.synyx.sybil.common;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.neo4j.helpers.collection.IteratorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.in.Status;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * IPConnectionRegistry.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service // Annotated so Spring finds and injects it.
public class BrickRegistry {

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(BrickRegistry.class);

    // map that contains all the Connections with the Domains as their key
    private Map<BrickDomain, IPConnection> ipConnections = new HashMap<>();

    // set that contains each registry that accesses this registry
    private Set<BrickletRegistry> registries = new HashSet<>();

    private BrickRepository brickRepository;
    private GraphDatabaseService graphDatabaseService;

    /**
     * Instantiates a new Brick registry.
     *
     * @param  brickRepository  The brick repository
     * @param  graphDatabaseService  The graph database service
     */
    @Autowired
    public BrickRegistry(BrickRepository brickRepository, GraphDatabaseService graphDatabaseService) {

        this.brickRepository = brickRepository;
        this.graphDatabaseService = graphDatabaseService;
    }

    /**
     * Register a connection to a Tinkerforge Brick.
     *
     * @param  brickDomain  the Brick's domain object.
     * @param  brickletRegistry  the bricklet registry
     *
     * @return  the iP connection
     */
    public IPConnection get(BrickDomain brickDomain, BrickletRegistry brickletRegistry) {

        registries.add(brickletRegistry);

        connect(brickDomain);

        return ipConnections.get(brickDomain);
    }


    /**
     * Register a connection to a Tinkerforge Brick.
     *
     * @param  brickDomain  the Brick's domain object.
     *
     * @return  the iP connection
     */
    public IPConnection get(BrickDomain brickDomain) {

        connect(brickDomain);

        return ipConnections.get(brickDomain);
    }


    /**
     * Connect a brick and put it's connection into the HashMap.
     *
     * @param  brickDomain  The Domain of the Brick to be connected
     */
    private void connect(BrickDomain brickDomain) {

        if (!ipConnections.containsKey(brickDomain)) { // if it isn't in the Map yet...

            IPConnection ipConnection = new IPConnection(); // ... make a new one...

            try {
                ipConnection.connect(brickDomain.getHostname(), brickDomain.getPort()); // ... connect it ...

                ConnectionListener connectionListener = new ConnectionListener(ipConnection);

                ipConnection.addConnectedListener(connectionListener);
                ipConnections.put(brickDomain, ipConnection); // ... and add it to the map.
            } catch (IOException e) {
                LOG.error("I/O Exception connecting to brick {}: {}", brickDomain.getName(), e.getMessage());
                HealthController.setHealth(Status.CRITICAL, "brick" + brickDomain.getName());
            } catch (AlreadyConnectedException e) {
                LOG.info("IPConnection to {} already connected: {}", brickDomain.getHostname(), e.toString());
            }
        }
    }


    /**
     * Connect all bricks.
     */
    public void connectAll() {

        LOG.debug("Connecting all bricks.");

        List<BrickDomain> brickDomains;

        try(Transaction tx = graphDatabaseService.beginTx()) { // begin transaction

            // get all Bricks from database and cast them into a list so that they're actually fetched
            brickDomains = new ArrayList<>(IteratorUtil.asCollection(brickRepository.findAll()));

            // end transaction
            tx.success();
        }

        for (BrickDomain brickDomain : brickDomains) {
            connect(brickDomain);
        }
    }


    /**
     * Disconnect all bricks, clear all the registered bricklets.
     */
    public void disconnectAll() {

        LOG.debug("Disconnecting all bricks and bricklets.");

        // disconnect all the IPConnections
        for (IPConnection ipConnection : ipConnections.values()) {
            try {
                ipConnection.disconnect();
            } catch (NotConnectedException e) {
                LOG.info("IPConnection {} already disconnected: {}", ipConnection.toString(), e.toString());
            }
        }

        // clear the list of connections
        ipConnections.clear();

        // clear all the registries, so bricklets will have to be re-registered
        for (BrickletRegistry brickletRegistry : registries) {
            brickletRegistry.clear();
        }
    }


    /**
     * Dis- and then reconnect all bricks.
     */
    public void reconnectAll() {

        disconnectAll();
        connectAll();
    }
}
