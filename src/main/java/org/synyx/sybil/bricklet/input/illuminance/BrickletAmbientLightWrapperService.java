package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.IPConnection;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.input.illuminance.domain.Illuminance;


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

        IPConnection ipConnection = brickService.connect(illuminance.getBrick());

        return new BrickletAmbientLightWrapper(illuminance.getUid(), ipConnection);
    }
}
