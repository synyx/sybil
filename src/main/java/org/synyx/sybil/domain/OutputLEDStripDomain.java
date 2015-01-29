package org.synyx.sybil.domain;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;


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

    private int length;

    @Fetch
    @RelatedTo(type = "IS_PART_OF")
    private BrickDomain brickDomain;

    /**
     * DO NOT CALL THIS! Exists only to placate Neo4j.
     */
    protected OutputLEDStripDomain() {
    }


    /**
     * Instantiates a new OutputLEDStrip domain.
     *
     * @param  name  The name to address the Bricklet with, always lowercase!
     * @param  uid  The Bricklet's UID
     * @param  length  The length, i.e. the number of LEDs
     * @param  brickDomain  The domain associated with the connected Brick
     */
    public OutputLEDStripDomain(String name, String uid, int length, BrickDomain brickDomain) {

        this.name = name.toLowerCase(); // Names are always lowercase!
        this.uid = uid;
        this.length = length;
        this.brickDomain = brickDomain;
    }

    /**
     * Gets the name under which the Bricklet is addressable.
     *
     * @return  The name
     */
    public String getName() {

        return name;
    }


    /**
     * Gets the Bricklet's UID.
     *
     * @return  The Bricklet's UID
     */
    public String getUid() {

        return uid;
    }


    /**
     * Gets length of the LED Strip, i.e. the number of LEDs.
     *
     * @return  The length
     */
    public int getLength() {

        return length;
    }


    /**
     * Gets the BrickDomain of the connected Brick.
     *
     * @return  the brick domain
     */
    public BrickDomain getBrickDomain() {

        return brickDomain;
    }
}
