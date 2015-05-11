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

    private String uid;

    /**
     * DO NOT CALL THIS! Exists only to placate Neo4j.
     */
    protected BrickDomain() {
    }


    /**
     * Instantiates a new Brick domain.
     *
     * @param  hostname  The hostname
     * @param  uid  The uid
     * @param  port  The port
     * @param  name  The name
     */
    public BrickDomain(String hostname, String uid, int port, String name) {

        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.uid = uid;
    }


    /**
     * Instantiates a new Brick domain.
     *
     * @param  hostname  The hostname
     * @param  uid  The uid
     * @param  port  The port (optional, defaults to 4223)
     */
    public BrickDomain(String hostname, String uid, int port) {

        this.hostname = hostname;
        this.name = hostname;
        this.port = port;
        this.uid = uid;
    }


    /**
     * Instantiates a new Brick domain.
     *
     * @param  hostname  The hostname
     * @param  uid  The uid
     */
    public BrickDomain(String hostname, String uid) {

        this.hostname = hostname;
        this.name = hostname;
        this.uid = uid;
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


    /**
     * Gets uid.
     *
     * @return  the uid
     */
    public String getUid() {

        return uid;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BrickDomain that = (BrickDomain) o;

        return port == that.port && hostname.equals(that.hostname) && id.equals(that.id) && name.equals(that.name)
            && uid.equals(that.uid);
    }


    @Override
    public int hashCode() {

        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + hostname.hashCode();
        result = 31 * result + port;
        result = 31 * result + uid.hashCode();

        return result;
    }
}
