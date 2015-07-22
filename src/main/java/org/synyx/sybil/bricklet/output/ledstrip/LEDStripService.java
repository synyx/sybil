package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceDTOService;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceService;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDomain;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * LEDStripService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class LEDStripService {

    private static final int SIXTEEN = 16;
    private static final int TEN = 10;
    private static final short MAX_PRIMARY_COLOR = (short) 255; // NOSONAR Tinkerforge library uses shorts
    private static final double DEFAULT_BRIGHTNESS = 1.0;

    private final BrickletProvider brickletProvider;
    private final IlluminanceDTOService illuminanceDTOService;
    private final IlluminanceService illuminanceService;

    @Autowired
    public LEDStripService(BrickletProvider provider, IlluminanceDTOService illuminanceDTOService,
        IlluminanceService illuminanceService) {

        this.brickletProvider = provider;
        this.illuminanceDTOService = illuminanceDTOService;
        this.illuminanceService = illuminanceService;
    }

    public List<Color> getPixels(LEDStripDTO ledStripDTO) throws AlreadyConnectedException, TimeoutException,
        NotConnectedException, IOException {

        LEDStripDomain ledStripDomain = ledStripDTO.getDomain();
        List<Color> result = new ArrayList<>();

        BrickletLEDStripWrapper brickletLEDStrip = brickletProvider.getBrickletLEDStrip(ledStripDomain);

        for (int pos = 0; pos < ledStripDomain.getLength(); pos += SIXTEEN) {
            BrickletLEDStrip.RGBValues values = brickletLEDStrip.getRGBValues(pos, (short) SIXTEEN); // NOSONAR Tinkerforge library uses shorts

            for (int i = 0; i < Math.min(ledStripDomain.getLength() - pos, SIXTEEN); i++) {
                result.add(Color.colorFromLEDStrip(values, i));
            }
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
        int spriteMaxSize = Math.min(pixelBufferSize, sprite.getLength());

        final int[] pixelBufferRed = new int[pixelBufferSize];
        final int[] pixelBufferGreen = new int[pixelBufferSize];
        final int[] pixelBufferBlue = new int[pixelBufferSize];

        // Copy the sprite's content into the pixelbuffer
        System.arraycopy(sprite.getRed(), 0, pixelBufferRed, 0, spriteMaxSize);
        System.arraycopy(sprite.getGreen(), 0, pixelBufferGreen, 0, spriteMaxSize);
        System.arraycopy(sprite.getBlue(), 0, pixelBufferBlue, 0, spriteMaxSize);

        drawSprite(ledStripDomain, pixelBufferRed, pixelBufferGreen, pixelBufferBlue);
    }


    private void drawSprite(LEDStripDomain ledStripDomain, int[] pixelBufferRed, int[] pixelBufferGreen,
        int[] pixelBufferBlue) throws TimeoutException, NotConnectedException, IOException, AlreadyConnectedException {

        short[] transferBufferRed; // NOSONAR Tinkerforge library uses shorts
        short[] transferBufferGreen; // NOSONAR Tinkerforge library uses shorts
        short[] transferBufferBlue; // NOSONAR Tinkerforge library uses shorts

        double brightness = DEFAULT_BRIGHTNESS;

        if (ledStripDomain.hasSensor()) {
            brightness = getBrightness(ledStripDomain);
        }

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


    private double getBrightness(LEDStripDomain ledStripDomain) throws IOException, AlreadyConnectedException,
        NotConnectedException, TimeoutException {

        double brightness = DEFAULT_BRIGHTNESS;

        String sensor = ledStripDomain.getSensor();

        IlluminanceDTO illuminanceDTO = illuminanceDTOService.getDTO(sensor);

        IlluminanceDomain illuminanceDomain = illuminanceDTO.getDomain();

        // since the sensor reports in lux / 10, we have to multiply the threshold and divide the multiplier by 10 each.
        int thresholdInDecilux = illuminanceDomain.getThreshold() * TEN;
        double multiplier = illuminanceDomain.getMultiplier() / TEN;

        int illuminance = illuminanceService.getIlluminance(illuminanceDTO);

        if (illuminance < thresholdInDecilux) {
            brightness += (thresholdInDecilux - illuminance) * multiplier;
        }

        return brightness;
    }


    private short[] applyBrightnessAndCastToShort(int[] pixels, double brightness) { // NOSONAR Tinkerforge library uses shorts

        short[] result = new short[pixels.length]; // NOSONAR Tinkerforge library uses shorts

        for (int index = 0; index < pixels.length; index++) {
            if (brightness == DEFAULT_BRIGHTNESS) {
                result[index] = (short) pixels[index]; // NOSONAR Tinkerforge library uses shorts
            } else {
                result[index] = setColorLimits((short) (pixels[index] * brightness)); // NOSONAR Tinkerforge library uses shorts
            }
        }

        return result;
    }


    private short setColorLimits(short primaryColor) { // NOSONAR Tinkerforge library uses shorts

        if (primaryColor > MAX_PRIMARY_COLOR) {
            return MAX_PRIMARY_COLOR;
        }

        return primaryColor;
    }
}
