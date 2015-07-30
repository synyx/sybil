package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.IPConnection;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;


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

    public BrickletAmbientLightWrapper getBrickletAmbientLight(IlluminanceConfig illuminanceConfig) {

        IPConnection ipConnection = brickDTOService.connect(illuminanceConfig.getBrick());

        return new BrickletAmbientLightWrapper(illuminanceConfig.getUid(), ipConnection);
    }
}
