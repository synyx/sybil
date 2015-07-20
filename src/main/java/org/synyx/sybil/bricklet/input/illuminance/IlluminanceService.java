package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDomain;

import java.io.IOException;


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

    public int getIlluminance(IlluminanceDTO illuminanceDTO) throws AlreadyConnectedException, TimeoutException,
        NotConnectedException, IOException {

        IlluminanceDomain illuminanceDomain = illuminanceDTO.getDomain();

        BrickletAmbientLightWrapper brickletAmbientLight = brickletProvider.getBrickletAmbientLight(illuminanceDomain);

        int illuminance = brickletAmbientLight.getIlluminance();

        brickletAmbientLight.disconnect();

        return illuminance;
    }
}
