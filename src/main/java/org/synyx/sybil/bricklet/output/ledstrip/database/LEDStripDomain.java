package org.synyx.sybil.bricklet.output.ledstrip.database;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.DeviceDomain;


/**
 * LEDStrip domain. Persistence for the LED Strip data, but not the actual LEDStrip object.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Relation(collectionRelation = "ledstrips")
public class LEDStripDomain implements DeviceDomain {

    private String name;

    private String uid;

    private int length;

    private String brick;

    protected LEDStripDomain() {

        // Default constructor deliberately left empty
    }


    /**
     * Instantiates a new LEDStrip domain.
     *
     * @param  name  The name to address the Bricklet with, always lowercase!
     * @param  uid  The Bricklet's UID
     * @param  length  The length, i.e. the number of LEDs
     * @param  brick  The name of the connected Brick
     */
    public LEDStripDomain(String name, String uid, int length, String brick) {

        this.name = name.toLowerCase();
        this.uid = uid;
        this.length = length;
        this.brick = brick;
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


    public String getBrick() {

        return brick;
    }
}
