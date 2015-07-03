package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.bricklet.Bricklet;
import org.synyx.sybil.jenkins.domain.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Controls a single LED strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStrip implements Bricklet {

    private static final Logger LOG = LoggerFactory.getLogger(LEDStrip.class);

    private static final double MAX_BRIGHTNESS = 255.0;
    private static final double MIN_BRIGHTNESS = 0.0;
    private static final short MAX_PRIMARY_COLOR = (short) 255;
    private static final int SIXTEEN = 16;

    private final BrickletLEDStrip ledStrip;
    private final short[] pixelBufferRed;
    private final short[] pixelBufferGreen;
    private final short[] pixelBufferBlue;
    private double brightness;
    private final int length;
    private String name;

    /**
     * Makes new LEDStrip object.
     *
     * @param  ledStrip  The LED Strip we want to control.
     * @param  length  How many LEDs are on the LED Strip.
     * @param  name  The name to address the LED Strip with, always lowercase!
     */
    public LEDStrip(BrickletLEDStrip ledStrip, int length, String name) {

        this.name = name.toLowerCase();
        brightness = 1.0;
        this.ledStrip = ledStrip;
        this.length = length;

        // make sure the length is a multiple of sixteen
        int differenceToMultipleOfSixteen = length % SIXTEEN;

        int newlength = length + (SIXTEEN - differenceToMultipleOfSixteen);

        pixelBufferRed = new short[newlength];
        pixelBufferGreen = new short[newlength];
        pixelBufferBlue = new short[newlength];
    }

    @Override
    public String getName() {

        return name;
    }


    public int getLength() {

        return length;
    }


    public double getBrightness() {

        return brightness;
    }


    public List<Color> getPixelBuffer() {

        List<Color> pixelBuffer = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            pixelBuffer.add(new Color(pixelBufferRed[i], pixelBufferGreen[i], pixelBufferBlue[i]));
        }

        return pixelBuffer;
    }


    /**
     * Shows a single color on the whole LED Strip.
     *
     * @param  color  The color the strip should be
     */
    public void setFill(Color color) {

        LOG.debug("Setting LEDstrip {} to color {}", name, color);

        Arrays.fill(pixelBufferRed, color.getRedAsShort());
        Arrays.fill(pixelBufferGreen, color.getGreenAsShort());
        Arrays.fill(pixelBufferBlue, color.getBlueAsShort());
    }


    /**
     * Sets the brightness of the LEDs.
     *
     * @param  brightness  Brightness, between 0.0 (completely black) and 255.0 (maximum brightness)
     */
    public void setBrightness(double brightness) {

        LOG.debug("Setting brightness of LEDstrip {} to {}", name, brightness);

        this.brightness = setBrightnessLimits(brightness);
    }


    private double setBrightnessLimits(double brightness) {

        if (brightness < MIN_BRIGHTNESS) {
            return MIN_BRIGHTNESS;
        }

        if (brightness > MAX_BRIGHTNESS) {
            return MAX_BRIGHTNESS;
        }

        return brightness;
    }


    public void setPixelColor(int positionOnLedStrip, Color color) {

        LOG.debug("Setting pixel {} of LEDstrip {} to {}", positionOnLedStrip, name, color);

        pixelBufferRed[positionOnLedStrip] = color.getRedAsShort();
        pixelBufferGreen[positionOnLedStrip] = color.getGreenAsShort();
        pixelBufferBlue[positionOnLedStrip] = color.getBlueAsShort();
    }


    public Color getPixelColor(int positionOnLedStrip) {

        LOG.debug("Retrieving color of pixel {} of LEDstrip {}", positionOnLedStrip, name);

        Color color = null;

        try {
            color = Color.colorFromLedStrip(ledStrip.getRGBValues(positionOnLedStrip, (short) 1));
            setHealthOkay();
        } catch (TimeoutException | NotConnectedException exception) {
            logConnectionError(exception);
        }

        return color;
    }


    private void logConnectionError(TinkerforgeException exception) {

        LOG.error("Error connecting to LEDStrip {}: {}", name, exception.toString());
        HealthController.setHealth(Status.WARNING, name);
    }


    private void setHealthOkay() {

        HealthController.setHealth(Status.OKAY, name);
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

        for (int positionOnLedStrip = 0; positionOnLedStrip < pixelBufferRed.length; positionOnLedStrip += SIXTEEN) {
            redArray = Arrays.copyOfRange(pixelBufferRed, positionOnLedStrip, positionOnLedStrip + SIXTEEN);
            greenArray = Arrays.copyOfRange(pixelBufferGreen, positionOnLedStrip, positionOnLedStrip + SIXTEEN);
            blueArray = Arrays.copyOfRange(pixelBufferBlue, positionOnLedStrip, positionOnLedStrip + SIXTEEN);

            redArray = applyBrightness(redArray);
            greenArray = applyBrightness(greenArray);
            blueArray = applyBrightness(blueArray);

            try {
                ledStrip.setRGBValues(positionOnLedStrip, (short) SIXTEEN, blueArray, redArray, greenArray);
                setHealthOkay();
            } catch (TimeoutException | NotConnectedException exception) {
                logConnectionError(exception);
            }
        }
    }


    private short[] applyBrightness(short[] sixteenPixels) {

        for (int index = 0; index < SIXTEEN; index++) {
            sixteenPixels[index] *= brightness;

            if (sixteenPixels[index] > MAX_PRIMARY_COLOR) {
                sixteenPixels[index] = MAX_PRIMARY_COLOR;
            }
        }

        return sixteenPixels;
    }


    public void drawSprite(Sprite1D sprite, int positionOnLedStrip) {

        drawSprite(sprite, positionOnLedStrip, false);
    }


    public void drawSpriteWithWrap(Sprite1D sprite, int positionOnLedStrip) {

        drawSprite(sprite, positionOnLedStrip, true);
    }


    private void drawSprite(Sprite1D sprite, int position, boolean wrap) {

        LOG.debug("Drawing Sprite {} to LEDstrip {}", sprite.getName(), name);

        int positionOnSprite = 0;
        int positionOnPixelBuffer = position;

        while (positionOnSprite < sprite.getLength()) {
            if (positionOnPixelBuffer < this.length) {
                copySpriteColorToPixelBuffer(sprite, positionOnSprite, positionOnPixelBuffer);
            } else if (wrap) {
                // reset position to beginning of buffer
                positionOnPixelBuffer = 0;
                copySpriteColorToPixelBuffer(sprite, positionOnSprite, positionOnPixelBuffer);
            } else {
                break;
            }

            positionOnSprite++;
            positionOnPixelBuffer++;
        }
    }


    private void copySpriteColorToPixelBuffer(Sprite1D sprite, int positionOnSprite, int positionOnPixelBuffer) {

        pixelBufferRed[positionOnPixelBuffer] = sprite.getRed()[positionOnSprite];
        pixelBufferGreen[positionOnPixelBuffer] = sprite.getGreen()[positionOnSprite];
        pixelBufferBlue[positionOnPixelBuffer] = sprite.getBlue()[positionOnSprite];
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LEDStrip that = (LEDStrip) o;

        return Objects.equals(length, that.length) && Objects.equals(ledStrip, that.ledStrip)
            && Objects.equals(name, that.name);
    }


    @Override
    public int hashCode() {

        return Objects.hash(ledStrip, length, name);
    }
}
