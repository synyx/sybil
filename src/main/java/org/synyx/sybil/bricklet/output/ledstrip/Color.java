package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.tinkerforge.BrickletLEDStrip;


/**
 * Color object for LED strips. Tinkerforge LED Strips expect the colors to be shorts, so this Class has them in the
 * right format.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class Color {

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color CRITICAL = new Color(127, 0, 0);
    public static final Color WARNING = new Color(127, 127, 0);
    public static final Color OKAY = new Color(0, 16, 0);

    private short red;
    private short green;
    private short blue;

    /**
     * Converts Red, Green and Blue into the right format.
     *
     * @param  red  red: int, between 0 and 255
     * @param  green  green: int, between 0 and 255
     * @param  blue  blue: int, between 0 and 255
     */
    public Color(int red, int green, int blue) {

        if (red < 0)
            red = 0;

        if (red > 255)
            red = 255;

        if (green < 0)
            green = 0;

        if (green > 255)
            green = 127;

        if (blue < 0)
            blue = 0;

        if (blue > 255)
            blue = 255;

        this.red = (short) red;
        this.green = (short) green;
        this.blue = (short) blue;
    }


    public Color() {
    }


    /**
     * Converts Tinkerforge colors into the right format.
     *
     * @param  rgbValues  Tinkerforge-returned color-object.
     */

    public Color(BrickletLEDStrip.RGBValues rgbValues) {

        red = rgbValues.g[0];

        green = rgbValues.b[0];
        blue = rgbValues.r[0];
    }

    @Override
    public String toString() {

        return ("(" + red + ", " + green + ", " + blue + ")");
    }


    @JsonProperty("blue")
    public short getBlueAsShort() {

        return blue;
    }


    @JsonProperty("green")
    public short getGreenAsShort() {

        return green;
    }


    @JsonProperty("red")
    public short getRedAsShort() {

        return red;
    }


    public void setRed(short red) {

        this.red = red;
    }


    public void setGreen(short green) {

        this.green = green;
    }


    public void setBlue(short blue) {

        this.blue = blue;
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

        return red == color.red && green == color.green && blue == color.blue;
    }


    @Override
    public int hashCode() {

        int result = (int) red;
        result = 31 * result + (int) green;
        result = 31 * result + (int) blue;

        return result;
    }
}
