package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.io.IOException;

import java.util.Arrays;


/**
 * Controls a single LED strip.
 *
 * @author  Tobias Theuer
 */
public class LEDOutput {

    private int length;
    private IPConnection ipConnection;
    private BrickletLEDStrip ledStrip;
    private short[] pixelsRed;
    private short[] pixelsGreen;
    private short[] pixelsBlue;

    /**
     * Makes new LEDOutput object.
     *
     * @param  host  IP address or hostname of the Tinkerforge brick.
     * @param  port  Port the Tinkerforge brick is listening at.
     * @param  uid  UID of the LED Strip (discoverable in the viewer)
     * @param  chipType  Type of the chip on the LED Strip, either 2801, 2811 or 2812
     * @param  frameDuration  How long one set of colors is shown, in milliseconds. Minimum is 10(?).
     * @param  length  How many LEDs are on the LED Strip.
     */
    public LEDOutput(String host, int port, String uid, int chipType, int frameDuration, int length) {

        this.length = length;

        ipConnection = new IPConnection();

        try {
            ipConnection.connect(host, port);

            ledStrip = new BrickletLEDStrip(uid, ipConnection);

            ledStrip.setChipType(chipType);

            ledStrip.setFrameDuration(frameDuration);

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
        } catch (IOException | NotConnectedException | TimeoutException | AlreadyConnectedException e) {
            e.printStackTrace();
        }
    }

    public void close() {

        try {
            this.ipConnection.disconnect();
        } catch (NotConnectedException e) {
            e.printStackTrace();
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
