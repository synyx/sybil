package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDomain;


/**
 * IlluminanceService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class IlluminanceService {

    BrickletProvider brickletProvider;

    @Autowired
    public IlluminanceService(BrickletProvider brickletProvider) {

        this.brickletProvider = brickletProvider;
    }

    public int getIlluminance(IlluminanceDTO illuminanceDTO) {

        IlluminanceDomain illuminanceDomain = illuminanceDTO.getDomain();

        BrickletAmbientLightWrapper brickletAmbientLight = brickletProvider.getBrickletAmbientLight(illuminanceDomain);

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
