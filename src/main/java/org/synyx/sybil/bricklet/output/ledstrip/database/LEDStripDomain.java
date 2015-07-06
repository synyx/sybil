package org.synyx.sybil.bricklet.output.ledstrip.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.DeviceDomain;
import org.synyx.sybil.brick.database.BrickDomain;

import java.util.Objects;


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

    protected LEDStripDomain() {

        // Default constructor deliberately left empty
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

        this.name = name.toLowerCase();
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

        return areFieldsEqual((LEDStripDomain) o);
    }


    private boolean areFieldsEqual(LEDStripDomain o) {

        return length == o.length && brickDomain.equals(o.brickDomain) && id.equals(o.id) && name.equals(o.name)
            && uid.equals(o.uid);
    }


    @Override
    public int hashCode() {

        return Objects.hash(id, name, uid, length, brickDomain);
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
