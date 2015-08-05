package org.synyx.sybil.bricklet.input.illuminance.persistence;

/**
 * IlluminanceDomain.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

public class Illuminance {

    private String name;
    private String uid;
    private int threshold;
    private double multiplier;
    private String brick;

    public Illuminance(String name, String uid, int threshold, double multiplier, String brick) {

        this.name = name;
        this.uid = uid;
        this.threshold = threshold;
        this.multiplier = multiplier;
        this.brick = brick;
    }


    public Illuminance() {

        // default constructor deliberately left empty
    }

    public String getName() {

        return name;
    }


    public void setName(String name) {

        this.name = name;
    }


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
