package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.input.illuminance.IlluminanceConnectionException;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceDTOService;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceService;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStrip;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

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

    private final BrickletLEDStripWrapperService brickletLEDStripWrapperService;
    private final IlluminanceDTOService illuminanceDTOService;
    private final IlluminanceService illuminanceService;
    private final LEDStripRepository ledStripRepository;

    @Autowired
    public LEDStripService(BrickletLEDStripWrapperService provider, IlluminanceDTOService illuminanceDTOService,
        IlluminanceService illuminanceService, LEDStripRepository ledStripRepository) {

        this.brickletLEDStripWrapperService = provider;
        this.illuminanceDTOService = illuminanceDTOService;
        this.illuminanceService = illuminanceService;
        this.ledStripRepository = ledStripRepository;
    }

    public List<Color> getPixels(String name) {

        LEDStrip ledStrip = ledStripRepository.get(name);
        List<Color> result = new ArrayList<>();

        BrickletLEDStripWrapper brickletLEDStrip = brickletLEDStripWrapperService.getBrickletLEDStrip(ledStrip);

        for (int pos = 0; pos < ledStrip.getLength(); pos += SIXTEEN) {
            BrickletLEDStrip.RGBValues values = getPixelValues(brickletLEDStrip, pos); // NOSONAR Tinkerforge library uses shorts

            for (int i = 0; i < Math.min(ledStrip.getLength() - pos, SIXTEEN); i++) {
                result.add(Color.colorFromLEDStrip(values, i));
            }
        }

        brickletLEDStrip.disconnect();

        return result;
    }


    public void turnOffAllLEDStrips() {

        List<LEDStrip> ledStrips = ledStripRepository.getAll();

        for (LEDStrip ledStrip : ledStrips) {
            turnOff(ledStrip);
        }
    }


    public void handleStatus(String name, StatusInformation statusInformation) {

        LEDStrip ledStrip = ledStripRepository.get(name);

        if (ledStrip == null) {
            throw new LEDStripNotFoundException("LED strip " + name + " not found");
        }

        Sprite1D sprite1D = new Sprite1D(ledStrip.getLength(), statusInformation.getSource());
        sprite1D.setFill(getColorFromStatus(ledStrip, statusInformation));

        drawSprite(ledStrip, sprite1D);
    }


    public void handleSprite(String name, Sprite1D sprite) {

        LEDStrip ledStrip = ledStripRepository.get(name);

        if (ledStrip == null) {
            throw new LEDStripNotFoundException("LED strip " + name + " not found");
        }

        drawSprite(ledStrip, sprite);
    }


    private BrickletLEDStrip.RGBValues getPixelValues(BrickletLEDStripWrapper brickletLEDStrip, int pos) {

        try {
            return brickletLEDStrip.getRGBValues(pos, (short) SIXTEEN); // NOSONAR Tinkerforge library uses shorts
        } catch (TimeoutException | NotConnectedException exception) {
            throw new LEDStripConnectionException("Error getting pixel values:", exception);
        }
    }


    private void turnOff(LEDStrip ledStrip) {

        Sprite1D sprite1D = new Sprite1D(ledStrip.getLength(), "OFF");
        sprite1D.setFill(Color.BLACK);

        drawSprite(ledStrip, sprite1D);
    }


    private Color getColorFromStatus(LEDStrip ledStrip, StatusInformation statusInformation) {

        if (ledStrip.hasCustomColors()) {
            Map<Status, Color> customColors = ledStrip.getCustomColors();

            return customColors.get(statusInformation.getStatus());
        } else {
            return Color.colorFromStatus(statusInformation.getStatus());
        }
    }


    private void drawSprite(LEDStrip ledStrip, Sprite1D sprite) {

        int pixelBufferSize = getPixelBufferSize(ledStrip);
        int spriteMaxSize = Math.min(pixelBufferSize, sprite.getLength());

        final int[] pixelBufferRed = new int[pixelBufferSize];
        final int[] pixelBufferGreen = new int[pixelBufferSize];
        final int[] pixelBufferBlue = new int[pixelBufferSize];

        // Copy the sprite's content into the pixelbuffer
        System.arraycopy(sprite.getRed(), 0, pixelBufferRed, 0, spriteMaxSize);
        System.arraycopy(sprite.getGreen(), 0, pixelBufferGreen, 0, spriteMaxSize);
        System.arraycopy(sprite.getBlue(), 0, pixelBufferBlue, 0, spriteMaxSize);

        short[] transferBufferRed; // NOSONAR Tinkerforge library uses shorts
        short[] transferBufferGreen; // NOSONAR Tinkerforge library uses shorts
        short[] transferBufferBlue; // NOSONAR Tinkerforge library uses shorts

        double brightness = DEFAULT_BRIGHTNESS;

        if (ledStrip.hasSensor()) {
            brightness = getBrightness(ledStrip);
        }

        BrickletLEDStripWrapper brickletLEDStrip = brickletLEDStripWrapperService.getBrickletLEDStrip(ledStrip);

        for (int positionOnLedStrip = 0; positionOnLedStrip < pixelBufferRed.length; positionOnLedStrip += SIXTEEN) {
            transferBufferRed = applyBrightnessAndCastToShort(Arrays.copyOfRange(pixelBufferRed, positionOnLedStrip,
                        positionOnLedStrip + SIXTEEN), brightness);
            transferBufferGreen = applyBrightnessAndCastToShort(Arrays.copyOfRange(pixelBufferGreen, positionOnLedStrip,
                        positionOnLedStrip + SIXTEEN), brightness);
            transferBufferBlue = applyBrightnessAndCastToShort(Arrays.copyOfRange(pixelBufferBlue, positionOnLedStrip,
                        positionOnLedStrip + SIXTEEN), brightness);

            try {
                brickletLEDStrip.setRGBValues(positionOnLedStrip, (short) SIXTEEN, // NOSONAR Tinkerforge uses shorts
                    transferBufferBlue, transferBufferRed, transferBufferGreen);
            } catch (TimeoutException | NotConnectedException exception) {
                throw new LEDStripConnectionException("Error setting pixel values:", exception);
            }
        }

        brickletLEDStrip.disconnect();
    }


    private int getPixelBufferSize(LEDStrip ledStrip) {

        int differenceToMultipleOfSixteen = ledStrip.getLength() % SIXTEEN;

        return ledStrip.getLength() + (SIXTEEN - differenceToMultipleOfSixteen);
    }


    private double getBrightness(LEDStrip ledStrip) {

        double brightness = DEFAULT_BRIGHTNESS;

        String sensor = ledStrip.getSensor();

        IlluminanceDTO illuminanceDTO = illuminanceDTOService.getDTO(sensor);

        IlluminanceConfig illuminanceConfig = illuminanceDTO.getConfig();

        /* since the sensor reports in lux / 10, we have to multiply the threshold and divide the multiplier by 10 each.
         * A multiplier of 1.0 results in an increase in brightness of 100% per Lux that is below the threshold.
         *      i.e. if the threshold is 20 Lux and the ambient illuminance is 19 Lux the brightness will be doubled.
         *      If the ambient illuminance is 18, the brightness will be tripled.
         */

        int thresholdInDecilux = illuminanceConfig.getThreshold() * TEN;
        double multiplier = illuminanceConfig.getMultiplier() / TEN;

        int illuminance;

        try {
            illuminance = illuminanceService.getIlluminance(illuminanceDTO);
        } catch (IlluminanceConnectionException exception) {
            throw new LEDStripConnectionException("Error getting ambient illuminance:", exception);
        }

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
