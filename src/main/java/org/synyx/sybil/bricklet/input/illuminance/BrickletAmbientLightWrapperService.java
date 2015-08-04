package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.IPConnection;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.bricklet.input.illuminance.domain.Illuminance;


/**
 * BrickletAmbientLightWrapperService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickletAmbientLightWrapperService {

    BrickDTOService brickDTOService;

    @Autowired
    public BrickletAmbientLightWrapperService(BrickDTOService brickDTOService) {

        this.brickDTOService = brickDTOService;
    }

    public BrickletAmbientLightWrapper getBrickletAmbientLight(Illuminance illuminance) {

        IPConnection ipConnection = brickDTOService.connect(illuminance.getBrick());

        return new BrickletAmbientLightWrapper(illuminance.getUid(), ipConnection);
    }
}
