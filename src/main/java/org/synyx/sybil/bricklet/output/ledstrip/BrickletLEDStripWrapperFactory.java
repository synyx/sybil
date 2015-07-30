package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripConfig;


/**
 * BrickletProvider.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickletLEDStripWrapperFactory {

    private static final int FRAME_DURATION = 10;
    private static final int CHIP_TYPE = 2812;

    private final BrickDTOService brickDTOService;

    @Autowired
    public BrickletLEDStripWrapperFactory(BrickDTOService brickDTOService) {

        this.brickDTOService = brickDTOService;
    }

    public BrickletLEDStripWrapper getBrickletLEDStrip(LEDStripConfig ledStripConfig) {

        IPConnection ipConnection = brickDTOService.connect(ledStripConfig.getBrick());

        BrickletLEDStripWrapper brickletLEDStrip = new BrickletLEDStripWrapper(ledStripConfig.getUid(), ipConnection);

        try {
            brickletLEDStrip.setFrameDuration(FRAME_DURATION);
            brickletLEDStrip.setChipType(CHIP_TYPE);
        } catch (TimeoutException | NotConnectedException exception) {
            throw new LoadFailedException("Error setting up LED strip:", exception);
        }

        return brickletLEDStrip;
    }
}
