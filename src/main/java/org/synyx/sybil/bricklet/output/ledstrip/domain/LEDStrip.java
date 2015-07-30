package org.synyx.sybil.bricklet.output.ledstrip.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.jenkins.domain.Status;

import java.util.HashMap;
import java.util.Map;


/**
 * LEDStrip domain.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

public class LEDStrip {

    private String name;
    private String uid;
    private int length;
    private String brick;

    @JsonProperty("sensor")
    private String sensor;
    private boolean hasSensor = false;

    private int okayRed;
    private int okayGreen;
    private int okayBlue;
    private int warningRed;
    private int warningGreen;
    private int warningBlue;
    private int criticalRed;
    private int criticalGreen;
    private int criticalBlue;

    private boolean hasCustomColors = false;

    protected LEDStrip() {

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
    public LEDStrip(String name, String uid, int length, String brick) {

        this.name = name.toLowerCase();
        this.uid = uid;
        this.length = length;
        this.brick = brick;
    }


    public LEDStrip(String name, String uid, int length, String brick, String sensor) {

        this.name = name;
        this.uid = uid;
        this.length = length;
        this.brick = brick;
        this.sensor = sensor;
        hasSensor = true;
    }

    public String getName() {

        return name;
    }


    public String getUid() {

        return uid;
    }


    public int getLength() {

        return length;
    }


    public String getBrick() {

        return brick;
    }


    @JsonIgnore
    public String getSensor() {

        return sensor;
    }


    public void setSensor(String sensor) {

        this.sensor = sensor;
        hasSensor = true;
    }


    public boolean hasSensor() {

        return hasSensor;
    }


    public boolean hasCustomColors() {

        return hasCustomColors;
    }


    public void setOkayRed(int okayRed) {

        this.okayRed = okayRed;
        hasCustomColors = true;
    }


    public void setOkayGreen(int okayGreen) {

        this.okayGreen = okayGreen;
        hasCustomColors = true;
    }


    public void setOkayBlue(int okayBlue) {

        this.okayBlue = okayBlue;
        hasCustomColors = true;
    }


    public void setWarningRed(int warningRed) {

        this.warningRed = warningRed;
        hasCustomColors = true;
    }


    public void setWarningGreen(int warningGreen) {

        this.warningGreen = warningGreen;
        hasCustomColors = true;
    }


    public void setWarningBlue(int warningBlue) {

        this.warningBlue = warningBlue;
        hasCustomColors = true;
    }


    public void setCriticalRed(int criticalRed) {

        this.criticalRed = criticalRed;
        hasCustomColors = true;
    }


    public void setCriticalGreen(int criticalGreen) {

        this.criticalGreen = criticalGreen;
        hasCustomColors = true;
    }


    public void setCriticalBlue(int criticalBlue) {

        this.criticalBlue = criticalBlue;
        hasCustomColors = true;
    }


    @JsonIgnore
    public Map<Status, Color> getCustomColors() {

        Map<Status, Color> customColors = new HashMap<>();
        customColors.put(Status.OKAY, getOkayColor());
        customColors.put(Status.WARNING, getWarningColor());
        customColors.put(Status.CRITICAL, getCriticalColor());

        return customColors;
    }


    private Color getOkayColor() {

        return new Color(okayRed, okayGreen, okayBlue);
    }


    private Color getWarningColor() {

        return new Color(warningRed, warningGreen, warningBlue);
    }


    private Color getCriticalColor() {

        return new Color(criticalRed, criticalGreen, criticalBlue);
    }
}
