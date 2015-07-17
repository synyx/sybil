package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.tinkerforge.BrickletLEDStrip;

import org.synyx.sybil.jenkins.domain.Status;

import java.util.Objects;


/**
 * Color object for LED strips.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class Color {

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color CRITICAL = new Color(127, 0, 0);
    public static final Color WARNING = new Color(127, 127, 0);
    public static final Color OKAY = new Color(0, 16, 0);

    private static final int MAX_PRIMARY_COLOR = 255;
    private static final int MIN_PRIMARY_COLOR = 0;

    private int red;
    private int green;
    private int blue;

    /**
     * Converts Red, Green and Blue into the right format.
     *
     * @param  red  red: int, between 0 and 255
     * @param  green  green: int, between 0 and 255
     * @param  blue  blue: int, between 0 and 255
     */
    public Color(int red, int green, int blue) {

        this.red = setColorLimits(red);
        this.green = setColorLimits(green);
        this.blue = setColorLimits(blue);
    }


    public Color() {

        // default constructor deliberately left empty.
    }

    private int setColorLimits(int primaryColor) {

        if (primaryColor < MIN_PRIMARY_COLOR) {
            return MIN_PRIMARY_COLOR;
        }

        if (primaryColor > MAX_PRIMARY_COLOR) {
            return MAX_PRIMARY_COLOR;
        }

        return primaryColor;
    }


    public static Color colorFromStatus(Status status) {

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
     * Converts Tinkerforge colors into the right format for the first pixel.
     *
     * @param  rgbValues  Tinkerforge-returned color-object.
     */
    public static Color colorFromLedStrip(BrickletLEDStrip.RGBValues rgbValues) {

        return colorFromLEDStrip(rgbValues, 0);
    }


    /**
     * Converts Tinkerforge colors into the right format.
     *
     * @param  rgbValues  Tinkerforge-returned color-object.
     * @param  position  The position on the LED strip.
     */
    public static Color colorFromLEDStrip(BrickletLEDStrip.RGBValues rgbValues, int position) {

        // WS2812 use BRG instead of RGB (think it through, this makes sense)
        int red = rgbValues.g[position];
        int green = rgbValues.b[position];
        int blue = rgbValues.r[position];

        return new Color(red, green, blue);
    }


    @JsonProperty("blue")
    public int getBlue() {

        return blue;
    }


    @JsonProperty("green")
    public int getGreen() {

        return green;
    }


    @JsonProperty("red")
    public int getRed() {

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

        Color color = (Color) o;

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
