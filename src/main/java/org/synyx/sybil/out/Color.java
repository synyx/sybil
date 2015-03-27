package org.synyx.sybil.out;

import com.tinkerforge.BrickletLEDStrip;


/**
 * Color object for LED strips. Tinkerforge LED Strips expect the colors to be shorts, so this Class has them in the
 * right format.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class Color {

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(127, 127, 127);
    public static final Color CRITICAL = new Color(127, 0, 0);
    public static final Color WARNING = new Color(127, 127, 0);
    public static final Color OKAY = new Color(0, 16, 0);

    private final short red;
    private final short green;
    private final short blue;

    /**
     * Converts Red, Green and Blue into the right format.
     *
     * @param  red  red: int, between 0 and 127
     * @param  green  green: int, between 0 and 127
     * @param  blue  blue: int, between 0 and 127
     */
    public Color(int red, int green, int blue) {

        if (red < 0)
            red = 0;

        if (red > 127)
            red = 127;

        if (green < 0)
            green = 0;

        if (green > 127)
            green = 127;

        if (blue < 0)
            blue = 0;

        if (blue > 127)
            blue = 127;

        this.red = (short) red;
        this.green = (short) green;
        this.blue = (short) blue;
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


    public short getBlueAsShort() {

        return blue;
    }


    public short getGreenAsShort() {

        return green;
    }


    public short getRedAsShort() {

        return red;
    }
}
