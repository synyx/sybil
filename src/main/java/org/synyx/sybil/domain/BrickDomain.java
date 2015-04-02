package org.synyx.sybil.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

import org.springframework.hateoas.core.Relation;


/**
 * IPConnection domain. Persistence for the Tinkerforge IPConnection data, but not the actual object.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "bricks")
public class BrickDomain {

    @GraphId
    private Long id;

    private String name;

    private String hostname;

    private int port = 4223; // default port

    /**
     * DO NOT CALL THIS! Exists only to placate Neo4j.
     */
    protected BrickDomain() {
    }


    /**
     * Instantiates a new Brick domain.
     *
     * @param  hostname  The hostname
     * @param  port  The port
     * @param  name  The name
     */
    public BrickDomain(String hostname, int port, String name) {

        this.name = name;
        this.hostname = hostname;
        this.port = port;
    }


    /**
     * Instantiates a new Brick domain.
     *
     * @param  hostname  The hostname
     * @param  port  The port (optional, defaults to 4223)
     */
    public BrickDomain(String hostname, int port) {

        this.hostname = hostname;
        this.name = hostname;
        this.port = port;
    }


    /**
     * Instantiates a new Brick domain.
     *
     * @param  hostname  The hostname
     */
    public BrickDomain(String hostname) {

        this.hostname = hostname;
        this.name = hostname;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BrickDomain that = (BrickDomain) o;

        if (port != that.port)
            return false;

        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null)
            return false;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }


    @Override
    public int hashCode() {

        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + port;

        return result;
    }


    /**
     * Gets the hostname of the brick.
     *
     * @return  The hostname
     */
    public String getHostname() {

        return hostname;
    }


    /**
     * Gets the port of the brick.
     *
     * @return  The port
     */
    public int getPort() {

        return port;
    }


    /**
     * Gets the name of the brick.
     *
     * @return  The name
     */
    public String getName() {

        return name;
    }
}
