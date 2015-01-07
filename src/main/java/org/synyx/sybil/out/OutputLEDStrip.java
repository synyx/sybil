package org.synyx.sybil.out;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


/**
 * Controls a single LED strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class OutputLEDStrip {

    private static final Logger LOG = LoggerFactory.getLogger(OutputLEDStrip.class);
    private final BrickletLEDStrip ledStrip;
    private short[] pixelBufferRed;
    private short[] pixelBufferGreen;
    private short[] pixelBufferBlue;
    private double brightness;
    private int length;
    private String uid = null;

    /**
     * Makes new OutputLEDStrip object.
     *
     * @param  ledStrip  The LED Strip we want to control.
     * @param  length  How many LEDs are on the LED Strip.
     */
    public OutputLEDStrip(BrickletLEDStrip ledStrip, int length) {

        try {
            uid = ledStrip.getIdentity().uid;
        } catch (TimeoutException | NotConnectedException e) {
            LOG.error("Error connecting to LEDStrip:", e);
        }

        brightness = 1.0;

        this.ledStrip = ledStrip;

        this.length = length;

        LOG.debug("Creating new OuputLEDStrip {}", uid);

        int differenceToMultipleOfSixteen = length % 16;

        if (differenceToMultipleOfSixteen > 0) {
            length = length + (16 - differenceToMultipleOfSixteen); // make sure the length is a multiple of sixteen
        }

        pixelBufferRed = new short[length];
        pixelBufferGreen = new short[length];
        pixelBufferBlue = new short[length];

        for (int i = 0; i < pixelBufferRed.length; i++) {
            pixelBufferRed[i] = (short) 0;
            pixelBufferGreen[i] = (short) 0;
            pixelBufferRed[i] = (short) 0;
        }
    }

    /**
     * Updates the LED Strip with the current content of the pixelbuffer. Needs to be called for changes made with a
     * setter to show.
     */
    public void updateDisplay() {

        LOG.debug("Updating display of LEDstrip {}", uid);

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
                LOG.error("Error connecting to LEDStrip:", e);
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

        LOG.debug("Setting brightness  of LEDstrip {} to {}", uid, brightness);

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

        LOG.debug("Setting pixel {} of LEDstrip {} to {}", position, uid, color);

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
    Color getPixel(int position) {

        LOG.debug("Retrieving color of pixel {} of LEDstrip {}", position, uid);

        Color color = null;

        try {
            color = new Color(ledStrip.getRGBValues(position, (short) 1));
        } catch (TimeoutException | NotConnectedException e) {
            LOG.error("Error connecting to LEDStrip:", e);
        }

        return color;
    }


    /**
     * Shows a single color on the whole LED Strip.
     *
     * @param  color  The color the strip should be
     */

    public void setColor(Color color) {

        LOG.debug("Setting LEDstrip {} to color {}", uid, color);

        Arrays.fill(pixelBufferRed, color.getRed());
        Arrays.fill(pixelBufferGreen, color.getGreen());
        Arrays.fill(pixelBufferBlue, color.getBlue());
    }
}
