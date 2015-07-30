package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;


/**
 * IlluminanceService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class IlluminanceService {

    BrickletAmbientLightWrapperService brickletAmbientLightWrapperService;

    @Autowired
    public IlluminanceService(BrickletAmbientLightWrapperService brickletAmbientLightWrapperService) {

        this.brickletAmbientLightWrapperService = brickletAmbientLightWrapperService;
    }

    public int getIlluminance(IlluminanceDTO illuminanceDTO) {

        IlluminanceConfig illuminanceConfig = illuminanceDTO.getConfig();

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
