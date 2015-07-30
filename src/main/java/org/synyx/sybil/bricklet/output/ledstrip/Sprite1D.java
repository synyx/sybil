package org.synyx.sybil.bricklet.output.ledstrip;

import org.apache.commons.lang3.builder.EqualsBuilder;

import org.springframework.hateoas.core.Relation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


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


    /**
     * Instantiates a new sprite with a given list of colors as pixels.
     *
     * @param  name  The name of the Sprite
     * @param  pixels  A list of Colors.
     */
    public Sprite1D(String name, List<Color> pixels) {

        this.length = pixels.size();
        red = new int[length];
        green = new int[length];
        blue = new int[length];
        this.name = name;

        int minimum = Math.min(pixels.size(), length);

        for (int i = 0; i < minimum; i++) {
            red[i] = pixels.get(i).getRed();
            green[i] = pixels.get(i).getGreen();
            blue[i] = pixels.get(i).getBlue();
        }
    }


    /**
     * Creates a new sprite, named "Unnamed", all black.
     *
     * @param  length  The number of pixels the sprite will be long
     */
    public Sprite1D(int length) {

        this(length, "Unnamed");
    }


    /**
     * Instantiates a new sprite, named "Unnamed", with a given list of colors as pixels.
     *
     * @param  pixels  A list of Colors.
     */
    public Sprite1D(List<Color> pixels) {

        this("Unnamed", pixels);
    }

    /**
     * Fills the sprite with a single color.
     *
     * @param  color  The color the sprite should be
     */

    public void setFill(Color color) {

        Arrays.fill(red, color.getRed());
        Arrays.fill(green, color.getGreen());
        Arrays.fill(blue, color.getBlue());
    }


    /**
     * Sets a single pixel on the sprite to a color.
     *
     * @param  position  The position of the pixel on the sprite.
     * @param  color  The color the pixel should be.
     */
    public void setPixel(int position, Color color) {

        red[position] = color.getRed();
        green[position] = color.getGreen();
        blue[position] = color.getBlue();
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


    /**
     * Get red.
     *
     * @return  the int [ ]
     */
    public int[] getRed() {

        return Arrays.copyOf(red, red.length);
    }


    /**
     * Get green.
     *
     * @return  the int [ ]
     */
    public int[] getGreen() {

        return Arrays.copyOf(green, green.length);
    }


    /**
     * Get blue.
     *
     * @return  the int [ ]
     */
    public int[] getBlue() {

        return Arrays.copyOf(blue, blue.length);
    }


    /**
     * Gets name.
     *
     * @return  the name
     */
    public String getName() {

        return name;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Sprite1D sprite1D = (Sprite1D) o;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(red, sprite1D.red);
        builder.append(green, sprite1D.green);
        builder.append(blue, sprite1D.blue);
        builder.append(length, sprite1D.length);
        builder.append(name, sprite1D.name);

        return builder.isEquals();
    }


    @Override
    public int hashCode() {

        return Objects.hash(red, green, blue, length, name);
    }
}
