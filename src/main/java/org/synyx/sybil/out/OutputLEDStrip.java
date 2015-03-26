package org.synyx.sybil.out;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.common.Bricklet;

import java.util.Arrays;


/**
 * Controls a single LED strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class OutputLEDStrip implements Bricklet {

    private static final Logger LOG = LoggerFactory.getLogger(OutputLEDStrip.class);
    private final BrickletLEDStrip ledStrip;
    private final short[] pixelBufferRed;
    private final short[] pixelBufferGreen;
    private final short[] pixelBufferBlue;
    private double brightness;
    private final int length;
    private String name;
    private boolean loggedError = false;

    /**
     * Makes new OutputLEDStrip object.
     *
     * @param  ledStrip  The LED Strip we want to control.
     * @param  length  How many LEDs are on the LED Strip.
     * @param  name  The name to address the LED Strip with, always lowercase!
     */
    public OutputLEDStrip(BrickletLEDStrip ledStrip, int length, String name) {

        this.name = name.toLowerCase();

        brightness = 1.0;

        this.ledStrip = ledStrip;

        this.length = length;

        LOG.debug("Creating new OutputLEDStrip {}", name);

        int differenceToMultipleOfSixteen = length % 16;

        if (differenceToMultipleOfSixteen > 0) {
            length = length + (16 - differenceToMultipleOfSixteen); // make sure the length is a multiple of sixteen
        }

        pixelBufferRed = new short[length];
        pixelBufferGreen = new short[length];
        pixelBufferBlue = new short[length];
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        OutputLEDStrip that = (OutputLEDStrip) o;

        if (length != that.length)
            return false;

        if (!ledStrip.equals(that.ledStrip))
            return false;

        if (!name.equals(that.name))
            return false;

        return true;
    }


    @Override
    public int hashCode() {

        int result = ledStrip.hashCode();
        result = 31 * result + length;
        result = 31 * result + name.hashCode();

        return result;
    }


    /**
     * Updates the LED Strip with the current content of the pixelbuffer. Needs to be called for changes made with a
     * setter to show.
     */
    public void updateDisplay() {

        LOG.debug("Updating display of LEDstrip {}", name);

        short[] redArray;
        short[] greenArray;
        short[] blueArray;

        for (int i = 0; i < pixelBufferRed.length; i += 16) { // loop over the pixelbuffer in steps of 16
            redArray = Arrays.copyOfRange(pixelBufferRed, i, i + 16);
            greenArray = Arrays.copyOfRange(pixelBufferGreen, i, i + 16);
            blueArray = Arrays.copyOfRange(pixelBufferBlue, i, i + 16);

            for (int j = 0; j < 16; j++) {
                redArray[j] *= brightness;
                greenArray[j] *= brightness;
                blueArray[j] *= brightness;
            }

            try {
                ledStrip.setRGBValues(i, (short) 16, blueArray, redArray, greenArray);
            } catch (TimeoutException | NotConnectedException e) {
                if (loggedError) {
                    LOG.warn("Error connecting to LEDStrip {} during updateDisplay: {}", name, e.toString());
                } else {
                    LOG.error("Error connecting to LEDStrip {} during updateDisplay: {}", name, e.toString());
                    loggedError = true;
                }
            }
        }
    }


    /**
     * Draws a one-dimensional sprite onto the LED Strip.
     *
     * @param  sprite  The sprite object
     * @param  position  The position to draw the sprite at, starting at 0 for the pixel closest to the controller
     * @param  wrap  whether the sprite wraps around the end of the LED strip and the rest is drawn at the beginning
     */
    public void drawSprite(Sprite1D sprite, int position, boolean wrap) {

        LOG.debug("Drawing Sprite {} to LEDstrip {}", sprite.getName(), name);

        int spriteLength = sprite.getLength();
        short[] red = sprite.getRed();
        short[] green = sprite.getGreen();
        short[] blue = sprite.getBlue();

        int i = 0;

        while (i < spriteLength) {
            if (position < this.length) {
                pixelBufferRed[position] = red[i];
                pixelBufferGreen[position] = green[i];
                pixelBufferBlue[position] = blue[i];
            } else if (wrap) {
                position = 0; // reset the position to the beginning of the LED strip
                pixelBufferRed[position] = red[i];
                pixelBufferGreen[position] = green[i];
                pixelBufferBlue[position] = blue[i];
            } else {
                break;
            }

            i++;
            position++;
        }
    }


    /**
     * Draws a one-dimensional sprite onto the LED Strip. Defaults to no wraparound.
     *
     * @param  sprite  The sprite object
     * @param  position  The position to draw the sprite at, starting at 0 for the pixel closest to the controller
     */
    public void drawSprite(Sprite1D sprite, int position) {

        drawSprite(sprite, position, false);
    }


    /**
     * Gets the length of the LED strip.
     *
     * @return  The length of the LED strip
     */
    public int getLength() {

        return length;
    }


    /**
     * Sets the brightness of the LEDs.
     *
     * @param  brightness  Brightness, between 0.0 (completely dark) and 2.0 (twice as bright as normal)
     */
    public void setBrightness(double brightness) {

        LOG.debug("Setting brightness of LEDstrip {} to {}", name, brightness);

        if (brightness < 0.0) {
            brightness = 0.0;
        }

        if (brightness > 2.0) {
            brightness = 2.0;
        }

        this.brightness = brightness;
    }


    /**
     * Sets a single pixel on the LED Strip to color.
     *
     * @param  position  The position of the pixel on the LED Strip, starting at 0 for the pixel closest to the
     *                   controller
     * @param  color  The color the pixel should be.
     */
    public void setPixel(int position, Color color) {

        LOG.debug("Setting pixel {} of LEDstrip {} to {}", position, name, color);

        pixelBufferRed[position] = color.getRed();
        pixelBufferGreen[position] = color.getGreen();
        pixelBufferBlue[position] = color.getBlue();
    }


    /**
     * Gets the color of the pixel at the specified position.
     *
     * @param  position  The position of the pixel on the LED Strip, starting at 0 for the pixel closest to the
     *                   controller
     *
     * @return  The color at the specified position.
     */
    public Color getPixel(int position) {

        LOG.debug("Retrieving color of pixel {} of LEDstrip {}", position, name);

        Color color = null;

        try {
            color = new Color(ledStrip.getRGBValues(position, (short) 1));
        } catch (TimeoutException | NotConnectedException e) {
            if (loggedError) {
                LOG.warn("Error connecting to LEDStrip {} during getPixel: {}", name, e.toString());
            } else {
                LOG.error("Error connecting to LEDStrip {} during getPixel: {}", name, e.toString());
                loggedError = true;
            }
        }

        return color;
    }


    /**
     * Shows a single color on the whole LED Strip.
     *
     * @param  color  The color the strip should be
     */

    public void setFill(Color color) {

        LOG.debug("Setting LEDstrip {} to color {}", name, color);

        Arrays.fill(pixelBufferRed, color.getRed());
        Arrays.fill(pixelBufferGreen, color.getGreen());
        Arrays.fill(pixelBufferBlue, color.getBlue());
    }


    @Override
    public String getName() {

        return name;
    }
}
