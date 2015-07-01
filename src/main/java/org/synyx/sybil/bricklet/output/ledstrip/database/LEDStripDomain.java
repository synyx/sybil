package org.synyx.sybil.bricklet.output.ledstrip.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.DeviceDomain;
import org.synyx.sybil.brick.database.BrickDomain;


/**
 * LEDStrip domain. Persistence for the LED Strip data, but not the actual LEDStrip object.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "ledstrips")
public class LEDStripDomain implements DeviceDomain {

    @GraphId
    private Long id;

    private String name;

    private String uid;

    private int length;

    @Fetch
    @RelatedTo(type = "IS_PART_OF")
    @JsonProperty("brick")
    private BrickDomain brickDomain;

    // DO NOT CALL THIS! Exists only to placate Neo4j.
    protected LEDStripDomain() {
    }


    /**
     * Instantiates a new LEDStrip domain.
     *
     * @param  name  The name to address the Bricklet with, always lowercase!
     * @param  uid  The Bricklet's UID
     * @param  length  The length, i.e. the number of LEDs
     * @param  brickDomain  The domain associated with the connected Brick
     */
    public LEDStripDomain(String name, String uid, int length, BrickDomain brickDomain) {

        this.name = name.toLowerCase(); // Names are always lowercase!
        this.uid = uid;
        this.length = length;
        this.brickDomain = brickDomain;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LEDStripDomain that = (LEDStripDomain) o;

        return length == that.length && brickDomain.equals(that.brickDomain) && id.equals(that.id)
            && name.equals(that.name) && uid.equals(that.uid);
    }


    @Override
    public int hashCode() {

        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + uid.hashCode();
        result = 31 * result + length;
        result = 31 * result + brickDomain.hashCode();

        return result;
    }


    @Override
    public String getName() {

        return name;
    }


    @Override
    public String getUid() {

        return uid;
    }


    public int getLength() {

        return length;
    }


    public BrickDomain getBrickDomain() {

        return brickDomain;
    }
}
