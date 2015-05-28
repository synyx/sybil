package org.synyx.sybil.bricklet.output.ledstrip;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

import org.springframework.hateoas.core.Relation;

import java.util.Arrays;
import java.util.List;


/**
 * One-dimensional Sprite: An array of pixels. Drawn onto a 1D display, e.g. a LED Strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@NodeEntity
@Relation(collectionRelation = "sprites")
public class Sprite1D {

    @GraphId
    private Long id;

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


    public Sprite1D(int length, String name, List<Color> pixels) {

        this.length = length;
        red = new short[length];
        green = new short[length];
        blue = new short[length];
        this.name = name;

        for (int i = 0; i < length; i++) {
            red[i] = pixels.get(i).getRedAsShort();
            green[i] = pixels.get(i).getGreenAsShort();
            blue[i] = pixels.get(i).getBlueAsShort();
        }
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
