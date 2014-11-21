package org.synyx.sybil.out;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.Arrays;


/**
 * Controls a single LED strip.
 *
 * @author  Tobias Theuer
 */
public class OutputLEDStrip {

    private BrickletLEDStrip ledStrip;
    private short[] pixelsRed;
    private short[] pixelsGreen;
    private short[] pixelsBlue;

    /**
     * Makes new OutputLEDStrip object.
     *
     * @param  ledStrip  The LED Strip we want to control.
     * @param  length  How many LEDs are on the LED Strip.
     */
    public OutputLEDStrip(BrickletLEDStrip ledStrip, int length) {

        this.ledStrip = ledStrip;

        int differenceToMultipleOfSixteen = length % 16;

        if (differenceToMultipleOfSixteen > 0) {
            length = length + (16 - differenceToMultipleOfSixteen);
        }

        pixelsRed = new short[length];
        pixelsGreen = new short[length];
        pixelsBlue = new short[length];

        for (int i = 0; i < pixelsRed.length; i++) {
            pixelsRed[i] = (short) 0;
            pixelsGreen[i] = (short) 0;
            pixelsRed[i] = (short) 0;
        }
    }

    /**
     * Shows a single color on the whole LED Strip.
     *
     * @param  color  The color the strip should be
     */

    public void setColor(Color color) {

        for (int i = 0; i < pixelsRed.length; i++) {
            pixelsRed[i] = color.getRed();
            pixelsGreen[i] = color.getGreen();
            pixelsBlue[i] = color.getBlue();
        }

        updateDisplay();
    }


    /**
     * Updated the LED Strip with the current content of the pixelbuffer.
     */
    private void updateDisplay() {

        short[] redArray;
        short[] greenArray;
        short[] blueArray;

        for (int i = 0; i < pixelsRed.length; i += 16) { // loop over the list in steps of 16
            redArray = Arrays.copyOfRange(pixelsRed, i, i + 16);
            greenArray = Arrays.copyOfRange(pixelsGreen, i, i + 16);
            blueArray = Arrays.copyOfRange(pixelsBlue, i, i + 16);

            try {
                ledStrip.setRGBValues(i, (short) 16, blueArray, redArray, greenArray);
            } catch (TimeoutException | NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Sets a single pixel on the LED Strip to color.
     *
     * @param  position  The position of the pixel on the LED Strip, starting at 0 for the pixel closest to the
     *                   controller
     * @param  color  The color the pixel should be.
     */
    public void setPixel(int position, Color color) {

        pixelsRed[position] = color.getRed();
        pixelsGreen[position] = color.getGreen();
        pixelsBlue[position] = color.getBlue();

        updateDisplay();
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

        Color color = null;

        try {
            color = new Color(ledStrip.getRGBValues(position, (short) 1));
        } catch (TimeoutException | NotConnectedException e) {
            e.printStackTrace();
        }

        return color;
    }
}
