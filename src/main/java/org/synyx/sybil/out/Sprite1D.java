package org.synyx.sybil.out;

import java.util.Arrays;


/**
 * One-dimensional Sprite: An array of pixels. Drawn onto a 1D display, e.g. a LED Strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class Sprite1D {

    private final short[] red;
    private final short[] green;
    private final short[] blue;
    private final int length;
    private final String name;

    /**
     * Creates a new sprite, all black.
     *
     * @param  length  The number of pixels the sprite will be long
     * @param  name  The name of the Sprite (optional)
     */
    public Sprite1D(int length, String name) {

        this.length = length;
        red = new short[length];
        green = new short[length];
        blue = new short[length];
        this.name = name;
    }


    /**
     * Creates a new sprite, all black.
     *
     * @param  length  The number of pixels the sprite will be long
     */
    public Sprite1D(int length) {

        this.length = length;
        red = new short[length];
        green = new short[length];
        blue = new short[length];
        name = "Unnamed";
    }

    /**
     * Fills the sprite with a single color.
     *
     * @param  color  The color the sprite should be
     */

    public void setFill(Color color) {

        Arrays.fill(red, color.getRedAsShort());
        Arrays.fill(green, color.getGreenAsShort());
        Arrays.fill(blue, color.getBlueAsShort());
    }


    /**
     * Sets a single pixel on the sprite to a color.
     *
     * @param  position  The position of the pixel on the sprite.
     * @param  color  The color the pixel should be.
     */
    public void setPixel(int position, Color color) {

        red[position] = color.getRedAsShort();
        green[position] = color.getGreenAsShort();
        blue[position] = color.getBlueAsShort();
    }


    /**
     * Gets the length of the sprite.
     *
     * @return  The length of the sprite
     */
    public int getLength() {

        return length;
    }


    /**
     * Gets the color of pixel at position.
     *
     * @param  position  Position of the pixel on the sprite
     *
     * @return  Color object
     */
    public Color getPixel(int position) {

        return new Color(red[position], green[position], blue[position]);
    }


    public short[] getRed() {

        return red;
    }


    public short[] getGreen() {

        return green;
    }


    public short[] getBlue() {

        return blue;
    }


    public String getName() {

        return name;
    }
}
