package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.IOException;

import java.util.Arrays;
import java.util.Map;


/**
 * LEDStripService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class LEDStripService {

    private static final int SIXTEEN = 16;
    private static final short MAX_PRIMARY_COLOR = (short) 255; // NOSONAR Tinkerforge library uses shorts

    private final BrickletProvider brickletProvider;

    @Autowired
    public LEDStripService(BrickletProvider provider) {

        this.brickletProvider = provider;
    }

    public Sprite1D getPixels(LEDStripDTO ledStripDTO) throws AlreadyConnectedException, TimeoutException,
        NotConnectedException, IOException {

        LEDStripDomain ledStripDomain = ledStripDTO.getDomain();
        Sprite1D result = new Sprite1D(ledStripDomain.getLength(), ledStripDomain.getName());

        BrickletLEDStripWrapper brickletLEDStrip = brickletProvider.getBrickletLEDStrip(ledStripDomain);

        BrickletLEDStrip.RGBValues values = brickletLEDStrip.getRGBValues(0, (short) ledStripDomain.getLength()); // NOSONAR Tinkerforge library uses shorts

        for (int i = 0; i < ledStripDomain.getLength(); i++) {
            result.setPixel(i, Color.colorFromLEDStrip(values, i));
        }

        brickletLEDStrip.disconnect();

        return result;
    }


    public void turnOff(LEDStripDTO ledStripDTO) throws TimeoutException, NotConnectedException,
        AttributeEmptyException, IOException, AlreadyConnectedException {

        LEDStripDomain ledStripDomain = ledStripDTO.getDomain();

        Sprite1D sprite1D = new Sprite1D(ledStripDomain.getLength(), "OFF");
        sprite1D.setFill(Color.BLACK);

        ledStripDTO.setSprite(sprite1D);

        handleSprite(ledStripDTO);
    }


    public void handleStatus(LEDStripDTO ledStripDTO) throws TimeoutException, NotConnectedException,
        AttributeEmptyException, IOException, AlreadyConnectedException {

        LEDStripDomain ledStripDomain = ledStripDTO.getDomain();
        StatusInformation statusInformation = ledStripDTO.getStatus();

        Sprite1D sprite1D = new Sprite1D(ledStripDomain.getLength(), statusInformation.getSource());
        sprite1D.setFill(getColorFromStatus(ledStripDomain, statusInformation));

        ledStripDTO.setSprite(sprite1D);

        handleSprite(ledStripDTO);
    }


    private Color getColorFromStatus(LEDStripDomain ledStripDomain, StatusInformation statusInformation) {

        if (ledStripDomain.hasCustomColors()) {
            Map<Status, Color> customColors = ledStripDomain.getCustomColors();

            return customColors.get(statusInformation.getStatus());
        } else {
            return Color.colorFromStatus(statusInformation.getStatus());
        }
    }


    public void handleSprite(LEDStripDTO ledStripDTO) throws TimeoutException, NotConnectedException, IOException,
        AlreadyConnectedException {

        LEDStripDomain ledStripDomain = ledStripDTO.getDomain();
        Sprite1D sprite = ledStripDTO.getSprite();

        int pixelBufferSize = getPixelBufferSize(ledStripDomain);

        final int[] pixelBufferRed = new int[pixelBufferSize];
        final int[] pixelBufferGreen = new int[pixelBufferSize];
        final int[] pixelBufferBlue = new int[pixelBufferSize];

        // Copy the sprite's content into the pixelbuffer
        System.arraycopy(sprite.getRed(), 0, pixelBufferRed, 0, sprite.getLength());
        System.arraycopy(sprite.getGreen(), 0, pixelBufferGreen, 0, sprite.getLength());
        System.arraycopy(sprite.getBlue(), 0, pixelBufferBlue, 0, sprite.getLength());

        drawSprite(ledStripDomain, pixelBufferRed, pixelBufferGreen, pixelBufferBlue);
    }


    private void drawSprite(LEDStripDomain ledStripDomain, int[] pixelBufferRed, int[] pixelBufferGreen,
        int[] pixelBufferBlue) throws TimeoutException, NotConnectedException, IOException, AlreadyConnectedException {

        short[] transferBufferRed; // NOSONAR Tinkerforge library uses shorts
        short[] transferBufferGreen; // NOSONAR Tinkerforge library uses shorts
        short[] transferBufferBlue; // NOSONAR Tinkerforge library uses shorts

        double brightness = getBrightness(ledStripDomain);
        BrickletLEDStripWrapper brickletLEDStrip = brickletProvider.getBrickletLEDStrip(ledStripDomain);

        for (int positionOnLedStrip = 0; positionOnLedStrip < pixelBufferRed.length; positionOnLedStrip += SIXTEEN) {
            transferBufferRed = applyBrightnessAndCastToShort(Arrays.copyOfRange(pixelBufferRed, positionOnLedStrip,
                        positionOnLedStrip + SIXTEEN), brightness);
            transferBufferGreen = applyBrightnessAndCastToShort(Arrays.copyOfRange(pixelBufferGreen, positionOnLedStrip,
                        positionOnLedStrip + SIXTEEN), brightness);
            transferBufferBlue = applyBrightnessAndCastToShort(Arrays.copyOfRange(pixelBufferBlue, positionOnLedStrip,
                        positionOnLedStrip + SIXTEEN), brightness);

            brickletLEDStrip.setRGBValues(positionOnLedStrip, (short) SIXTEEN, // NOSONAR Tinkerforge uses shorts
                transferBufferBlue, transferBufferRed, transferBufferGreen);
        }

        brickletLEDStrip.disconnect();
    }


    private int getPixelBufferSize(LEDStripDomain ledStripDomain) {

        int differenceToMultipleOfSixteen = ledStripDomain.getLength() % SIXTEEN;

        return ledStripDomain.getLength() + (SIXTEEN - differenceToMultipleOfSixteen);
    }


    private double getBrightness(LEDStripDomain ledStripDomain) {

        return 1.0; // TODO: Poll the associated sensor, if any.
    }


    private short[] applyBrightnessAndCastToShort(int[] pixels, double brightness) { // NOSONAR Tinkerforge library uses shorts

        short[] result = new short[pixels.length]; // NOSONAR Tinkerforge library uses shorts

        for (int index = 0; index < pixels.length; index++) {
            result[index] = (short) (pixels[index] * brightness); // NOSONAR Tinkerforge library uses shorts

            if (result[index] > MAX_PRIMARY_COLOR) {
                result[index] = MAX_PRIMARY_COLOR;
            }
        }

        return result;
    }
}
