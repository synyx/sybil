package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.tinkerforge.BrickletLEDStrip;

import org.synyx.sybil.jenkins.domain.Status;

import java.util.Objects;


/**
 * Color object for LED strips. Tinkerforge LED Strips expect the colors to be shorts, so this Class has them in the
 * right format.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class OldColor {

    public static final OldColor BLACK = new OldColor(0, 0, 0);
    public static final OldColor WHITE = new OldColor(255, 255, 255);
    public static final OldColor CRITICAL = new OldColor(127, 0, 0);
    public static final OldColor WARNING = new OldColor(127, 127, 0);
    public static final OldColor OKAY = new OldColor(0, 16, 0);

    private static final int MAX_PRIMARY_COLOR = 255;
    private static final int MIN_PRIMARY_COLOR = 0;

    private final short red; // NOSONAR Tinkerforge library uses shorts
    private final short green; // NOSONAR Tinkerforge library uses shorts
    private final short blue; // NOSONAR Tinkerforge library uses shorts

    /**
     * Converts Red, Green and Blue into the right format.
     *
     * @param  red  red: int, between 0 and 255
     * @param  green  green: int, between 0 and 255
     * @param  blue  blue: int, between 0 and 255
     */
    public OldColor(int red, int green, int blue) {

        this.red = setColorLimitsAndCastToShort(red);
        this.green = setColorLimitsAndCastToShort(green);
        this.blue = setColorLimitsAndCastToShort(blue);
    }

    private short setColorLimitsAndCastToShort(int primaryColor) { // NOSONAR Tinkerforge library uses shorts

        if (primaryColor < MIN_PRIMARY_COLOR) {
            return (short) MIN_PRIMARY_COLOR; // NOSONAR Tinkerforge library uses shorts
        }

        if (primaryColor > MAX_PRIMARY_COLOR) {
            return (short) MAX_PRIMARY_COLOR; // NOSONAR Tinkerforge library uses shorts
        }

        return (short) primaryColor; // NOSONAR Tinkerforge library uses shorts
    }


    public static OldColor colorFromStatus(Status status) {

        switch (status) {
            case CRITICAL:
                return CRITICAL;

            case WARNING:
                return WARNING;

            default:
                return OKAY;
        }
    }


    /**
     * Converts Tinkerforge colors into the right format.
     *
     * @param  rgbValues  Tinkerforge-returned color-object.
     */
    public static OldColor colorFromLedStrip(BrickletLEDStrip.RGBValues rgbValues) {

        short red = rgbValues.g[0]; // NOSONAR Tinkerforge library uses shorts
        short green = rgbValues.b[0]; // NOSONAR Tinkerforge library uses shorts
        short blue = rgbValues.r[0]; // NOSONAR Tinkerforge library uses shorts

        return new OldColor(red, green, blue);
    }


    @JsonProperty("blue")
    public short getBlueAsShort() { // NOSONAR Tinkerforge library uses shorts

        return blue;
    }


    @JsonProperty("green")
    public short getGreenAsShort() { // NOSONAR Tinkerforge library uses shorts

        return green;
    }


    @JsonProperty("red")
    public short getRedAsShort() { // NOSONAR Tinkerforge library uses shorts

        return red;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OldColor color = (OldColor) o;

        return Objects.equals(red, color.red) && Objects.equals(green, color.green) && Objects.equals(blue, color.blue);
    }


    @Override
    public int hashCode() {

        return Objects.hash(red, green, blue);
    }


    @Override
    public String toString() {

        return "(" + red + ", " + green + ", " + blue + ")";
    }
}
