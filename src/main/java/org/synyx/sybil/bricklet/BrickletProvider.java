package org.synyx.sybil.bricklet;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.bricklet.input.illuminance.BrickletAmbientLightWrapper;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDomain;
import org.synyx.sybil.bricklet.output.ledstrip.BrickletLEDStripWrapper;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;

import java.io.IOException;


/**
 * BrickletProvider.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickletProvider {

    private static final int FRAME_DURATION = 10;
    private static final int CHIP_TYPE = 2812;

    private final BrickService brickService;
    private final BrickDTOService brickDTOService;

    @Autowired
    public BrickletProvider(BrickService brickService, BrickDTOService brickDTOService) {

        this.brickService = brickService;
        this.brickDTOService = brickDTOService;
    }

    public BrickletLEDStripWrapper getBrickletLEDStrip(LEDStripDomain ledStripDomain) throws TimeoutException,
        NotConnectedException, IOException, AlreadyConnectedException {

        BrickDTO brickDTO = brickDTOService.getDTO(ledStripDomain.getBrick());

        IPConnection ipConnection = brickService.connect(brickDTO);

        BrickletLEDStripWrapper brickletLEDStrip = new BrickletLEDStripWrapper(ledStripDomain.getUid(), ipConnection);

        brickletLEDStrip.setFrameDuration(FRAME_DURATION);
        brickletLEDStrip.setChipType(CHIP_TYPE);

        return brickletLEDStrip;
    }


    public BrickletAmbientLightWrapper getBrickletAmbientLight(IlluminanceDomain illuminanceDomain)
        throws TimeoutException, NotConnectedException, IOException, AlreadyConnectedException {

        BrickDTO brickDTO = brickDTOService.getDTO(illuminanceDomain.getBrick());

        IPConnection ipConnection = brickService.connect(brickDTO);

        return new BrickletAmbientLightWrapper(illuminanceDomain.getUid(), ipConnection);
    }
}
