package org.synyx.sybil.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;


/**
 * IPConnection domain. Persistence for the Tinkerforge IPConnection data, but not the actual object.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
public class BrickDomain {

    @GraphId
    private Long id;

    private String hostname;

    private int port;

    /**
     * DO NOT CALL THIS! Exists only to placate Neo4j.
     */
    protected BrickDomain() {
    }


    /**
     * Instantiates a new IPConnection domain.
     *
     * @param  hostname  The hostname
     * @param  port  The port (optional, defaults to 4223)
     */
    public BrickDomain(String hostname, int port) {

        this.hostname = hostname;
        this.port = port;
    }


    /**
     * Instantiates a new IPConnection domain.
     *
     * @param  hostname  The hostname
     */
    public BrickDomain(String hostname) {

        this.hostname = hostname;
        this.port = 4223;
    }

    /**
     * Gets the hostname of the brick the LEDs are connected to.
     *
     * @return  The hostname
     */
    public String getHostname() {

        return hostname;
    }


    /**
     * Gets the port of the brick the LEDs are connected to.
     *
     * @return  The port
     */
    public int getPort() {

        return port;
    }
}