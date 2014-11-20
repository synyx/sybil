package org.synyx.sybil.out;

import com.tinkerforge.BrickletLEDStrip;


/**
 * Color object for LED strips. Tinkerforge LED Strips expect the colors to be shorts, to this Class has them in the
 * right format.
 *
 * @author  Tobias Theuerkeeps
 */
public class Color {

    public static final Color BLACK = new Color(0, 0, 0);

    private short red;
    private short green;
    private short blue;

    /**
     * Converts R, G and B into the right format.
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
            green = 255;

        if (blue < 0)
            blue = 0;

        if (blue > 255)
            blue = 255;

        this.red = (short) red;
        this.green = (short) green;
        this.blue = (short) blue;
    }


    public Color(BrickletLEDStrip.RGBValues rgbValues) {

        red = rgbValues.g[0];
        green = rgbValues.b[0];
        blue = rgbValues.r[0];
    }

    public short getBlue() {

        return blue;
    }


    public short getGreen() {

        return green;
    }


    public short getRed() {

        return red;
    }
}
