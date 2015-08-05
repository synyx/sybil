package org.synyx.sybil.bricklet.input.illuminance.service;

import com.tinkerforge.IPConnection;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.service.BrickConnectionException;
import org.synyx.sybil.brick.service.BrickNotFoundException;
import org.synyx.sybil.brick.service.BrickService;
import org.synyx.sybil.bricklet.input.illuminance.persistence.Illuminance;


/**
 * BrickletAmbientLightWrapperService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickletAmbientLightWrapperService {

    BrickService brickService;

    @Autowired
    public BrickletAmbientLightWrapperService(BrickService brickService) {

        this.brickService = brickService;
    }

    public BrickletAmbientLightWrapper getBrickletAmbientLight(Illuminance illuminance) {

        IPConnection ipConnection;

        try {
            ipConnection = brickService.connect(illuminance.getBrick());
        } catch (BrickConnectionException | BrickNotFoundException exception) {
            throw new IlluminanceConnectionException("Error connecting to brick:", exception);
        }

        return new BrickletAmbientLightWrapper(illuminance.getUid(), ipConnection);
    }
}
