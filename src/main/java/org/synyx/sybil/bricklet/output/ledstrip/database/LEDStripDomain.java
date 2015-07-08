package org.synyx.sybil.bricklet.output.ledstrip.database;

import org.springframework.hateoas.core.Relation;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.DeviceDomain;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.jenkins.domain.Status;

import java.util.HashMap;
import java.util.Map;


/**
 * LEDStrip domain.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Relation(collectionRelation = "ledstrips")
public class LEDStripDomain implements DeviceDomain {

    private String name;
    private String uid;
    private int length;
    private String brick;

    private int okayRed;
    private int okayGreen;
    private int okayBlue;
    private int warningRed;
    private int warningGreen;
    private int warningBlue;
    private int criticalRed;
    private int criticalGreen;
    private int criticalBlue;

    private boolean customColors = false;

    protected LEDStripDomain() {

        // Default constructor deliberately left empty
    }


    public LEDStripDomain(String name, String uid, int length, String brick, int okayRed, int okayGreen, int okayBlue,
        int warningRed, int warningGreen, int warningBlue, int criticalRed, int criticalGreen, int criticalBlue) {

        this.name = name;
        this.uid = uid;
        this.length = length;
        this.brick = brick;
        this.okayRed = okayRed;
        this.okayGreen = okayGreen;
        this.okayBlue = okayBlue;
        this.warningRed = warningRed;
        this.warningGreen = warningGreen;
        this.warningBlue = warningBlue;
        this.criticalRed = criticalRed;
        this.criticalGreen = criticalGreen;
        this.criticalBlue = criticalBlue;
        customColors = true;
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


    public boolean hasCustomColors() {

        return customColors;
    }


    public void setOkayRed(int okayRed) {

        this.okayRed = okayRed;
        customColors = true;
    }


    public void setOkayGreen(int okayGreen) {

        this.okayGreen = okayGreen;
        customColors = true;
    }


    public void setOkayBlue(int okayBlue) {

        this.okayBlue = okayBlue;
        customColors = true;
    }


    public void setWarningRed(int warningRed) {

        this.warningRed = warningRed;
        customColors = true;
    }


    public void setWarningGreen(int warningGreen) {

        this.warningGreen = warningGreen;
        customColors = true;
    }


    public void setWarningBlue(int warningBlue) {

        this.warningBlue = warningBlue;
        customColors = true;
    }


    public void setCriticalRed(int criticalRed) {

        this.criticalRed = criticalRed;
        customColors = true;
    }


    public void setCriticalGreen(int criticalGreen) {

        this.criticalGreen = criticalGreen;
        customColors = true;
    }


    public void setCriticalBlue(int criticalBlue) {

        this.criticalBlue = criticalBlue;
        customColors = true;
    }


    public Map<Status, Color> getCustomColors() {

        if (hasCustomColors()) {
            Map<Status, Color> customColors = new HashMap<>();
            customColors.put(Status.OKAY, getOkayColor());
            customColors.put(Status.WARNING, getWarningColor());
            customColors.put(Status.CRITICAL, getCriticalColor());

            return customColors;
        } else {
            throw new AttributeEmptyException("LED strip " + name + " has no custom colors.");
        }
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
