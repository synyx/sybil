package org.synyx.sybil.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;


/**
 * OutputLEDStrip domain. Persistence for the LED Strip data, but not the actual OutputLEDStrip object.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
public class OutputLEDStripDomain {

    @GraphId
    private Long id;

    private String name;

    private String uid;

    private String hostname;

    private int port;

    private int length;

    protected OutputLEDStripDomain() {
    }


    /**
     * Instantiates a new OutputLEDStrip domain.
     *
     * @param  name  The name
     * @param  uid  The Bricklet's UID
     * @param  length  The length, i.e. the number of LEDs
     * @param  hostname  The hostname
     * @param  port  The port (optional, defaults to 4223)
     */
    public OutputLEDStripDomain(String name, String uid, int length, String hostname, int port) {

        this.name = name;
        this.uid = uid;
        this.length = length;
        this.hostname = hostname;
        this.port = port;
    }


    /**
     * Instantiates a new OutputLEDStrip domain.
     *
     * @param  name  The name
     * @param  uid  The Bricklet's UID
     * @param  length  The length, i.e. the number of LEDs
     * @param  hostname  The hostname
     */
    public OutputLEDStripDomain(String name, String uid, int length, String hostname) {

        this.name = name;
        this.uid = uid;
        this.length = length;
        this.hostname = hostname;
        this.port = 4223;
    }

    /**
     * Gets the name.
     *
     * @return  The name
     */
    public String getName() {

        return name;
    }


    /**
     * Gets the BrickletLEDStrip's UID.
     *
     * @return  The uid
     */
    public String getUid() {

        return uid;
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


    /**
     * Gets length of the LED Strip, i.e. the number of LEDs.
     *
     * @return  The length
     */
    public int getLength() {

        return length;
    }
}
