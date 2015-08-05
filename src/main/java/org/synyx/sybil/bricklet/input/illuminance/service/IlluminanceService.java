package org.synyx.sybil.bricklet.input.illuminance.service;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.input.illuminance.persistence.Illuminance;
import org.synyx.sybil.bricklet.input.illuminance.persistence.IlluminanceRepository;


/**
 * IlluminanceService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class IlluminanceService {

    private static final double DEFAULT_BRIGHTNESS = 1.0;
    private static final int TEN = 10;

    private final BrickletAmbientLightWrapperService brickletAmbientLightWrapperService;
    private final IlluminanceRepository illuminanceRepository;

    @Autowired
    public IlluminanceService(BrickletAmbientLightWrapperService brickletAmbientLightWrapperService,
        IlluminanceRepository illuminanceRepository) {

        this.brickletAmbientLightWrapperService = brickletAmbientLightWrapperService;
        this.illuminanceRepository = illuminanceRepository;
    }

    public double getBrightness(String name) {

        double brightness = DEFAULT_BRIGHTNESS;

        Illuminance illuminanceConfig = illuminanceRepository.get(name);

        if (illuminanceConfig == null) {
            throw new IlluminanceNotFoundException("Illuminance sensor " + name + " not found.");
        }

        /* since the sensor reports in lux / 10, we have to multiply the threshold and divide the multiplier by 10 each.
         * A multiplier of 1.0 results in an increase in brightness of 100% per Lux that is below the threshold.
         *      i.e. if the threshold is 20 Lux and the ambient illuminance is 19 Lux the brightness will be doubled.
         *      If the ambient illuminance is 18, the brightness will be tripled.
         */

        int thresholdInDecilux = illuminanceConfig.getThreshold() * TEN;

        double multiplier = illuminanceConfig.getMultiplier() / TEN;

        int illuminance = getIlluminance(illuminanceConfig);

        if (illuminance < thresholdInDecilux) {
            brightness += (thresholdInDecilux - illuminance) * multiplier;
        }

        return brightness;
    }


    private int getIlluminance(Illuminance illuminanceConfig) {

        BrickletAmbientLightWrapper brickletAmbientLight = brickletAmbientLightWrapperService.getBrickletAmbientLight(
                illuminanceConfig);

        int illuminance;

        try {
            illuminance = brickletAmbientLight.getIlluminance();

            brickletAmbientLight.disconnect();
        } catch (TimeoutException | NotConnectedException exception) {
            throw new IlluminanceConnectionException("Error getting sensor value:", exception);
        }

        return illuminance;
    }
}
