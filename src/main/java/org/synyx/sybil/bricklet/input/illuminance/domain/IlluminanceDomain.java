package org.synyx.sybil.bricklet.input.illuminance.domain;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.DeviceDomain;


/**
 * IlluminanceDomain.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Relation(collectionRelation = "illuminances")
public class IlluminanceDomain implements DeviceDomain {

    private String name;
    private String uid;
    private int threshold;
    private double multiplier;
    private String brick;

    public IlluminanceDomain(String name, String uid, int threshold, double multiplier, String brick) {

        this.name = name;
        this.uid = uid;
        this.threshold = threshold;
        this.multiplier = multiplier;
        this.brick = brick;
    }


    public IlluminanceDomain() {

        // default constructor deliberately left empty
    }

    @Override
    public String getName() {

        return name;
    }


    public void setName(String name) {

        this.name = name;
    }


    @Override
    public String getUid() {

        return uid;
    }


    public void setUid(String uid) {

        this.uid = uid;
    }


    public int getThreshold() {

        return threshold;
    }


    public void setThreshold(int threshold) {

        this.threshold = threshold;
    }


    public double getMultiplier() {

        return multiplier;
    }


    public void setMultiplier(double multiplier) {

        this.multiplier = multiplier;
    }


    public String getBrick() {

        return brick;
    }


    public void setBrick(String brick) {

        this.brick = brick;
    }
}
