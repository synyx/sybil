package org.synyx.sybil.domain;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.LinkedHashSet;
import java.util.Set;


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

    @Fetch
    @RelatedTo(type = "HAS_BRICKLETS")
    private Set<BrickletDomain> bricklets = new LinkedHashSet<>();

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

    public void addBricklet(BrickletDomain brickletDomain) {

        bricklets.add(brickletDomain);
    }


    /**
     * Gets the bricklets connected to the Brick.
     *
     * @return  the bricklets
     */
    public Set<BrickletDomain> getBricklets() {

        return bricklets;
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
