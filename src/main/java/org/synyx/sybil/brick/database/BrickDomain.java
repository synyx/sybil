package org.synyx.sybil.brick.database;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.DeviceDomain;

import java.util.Objects;


/**
 * IPConnection domain. Persistence for the Tinkerforge IPConnection data, but not the actual object.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "bricks")
public class BrickDomain implements DeviceDomain {

    private static final int DEFAULT_PORT = 4223;

    @GraphId
    private Long id;

    private String name;

    private String hostname;

    private int port = DEFAULT_PORT;

    private String uid;

    protected BrickDomain() {

        // Default constructor deliberately left empty
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
    @Override
    public String getName() {

        return name;
    }


    /**
     * Gets uid.
     *
     * @return  the uid
     */
    @Override
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

        return areFieldsEqual((BrickDomain) o);
    }


    private boolean areFieldsEqual(BrickDomain o) {

        return port == o.port && hostname.equals(o.hostname) && id.equals(o.id) && name.equals(o.name)
            && uid.equals(o.uid);
    }


    @Override
    public int hashCode() {

        return Objects.hash(id, name, hostname, port, uid);
    }
}
