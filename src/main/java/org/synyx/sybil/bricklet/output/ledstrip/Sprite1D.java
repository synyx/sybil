package org.synyx.sybil.bricklet.output.ledstrip;

import org.springframework.hateoas.core.Relation;

import java.util.Arrays;
import java.util.List;


/**
 * One-dimensional Sprite: An array of pixels. Drawn onto a 1D display, e.g. a LED Strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Relation(collectionRelation = "sprites")
public class Sprite1D {

    private final int[] red;
    private final int[] green;
    private final int[] blue;
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
        red = new int[length];
        green = new int[length];
        blue = new int[length];
        this.name = name;
    }


    public Sprite1D(int length, String name, List<Color> pixels) {

        this.length = length;
        red = new int[length];
        green = new int[length];
        blue = new int[length];
        this.name = name;

        for (int i = 0; i < length; i++) {
            red[i] = pixels.get(i).getRed();
            green[i] = pixels.get(i).getGreen();
            blue[i] = pixels.get(i).getBlue();
        }
    }


    /**
     * Creates a new sprite, all black.
     *
     * @param  length  The number of pixels the sprite will be long
     */
    public Sprite1D(int length) {

        this(length, "Unnamed");
    }

    /**
     * Fills the sprite with a single color.
     *
     * @param  color  The color the sprite should be
     */

    public void setFill(OldColor color) {

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
    public void setPixel(int position, OldColor color) {

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


    public int[] getRed() {

        return Arrays.copyOf(red, red.length);
    }


    public int[] getGreen() {

        return Arrays.copyOf(green, green.length);
    }


    public int[] getBlue() {

        return Arrays.copyOf(blue, blue.length);
    }


    public String getName() {

        return name;
    }
}
