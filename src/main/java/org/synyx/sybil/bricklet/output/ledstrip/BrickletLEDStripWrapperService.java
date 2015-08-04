package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStrip;


/**
 * BrickletLEDStripWrapperService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickletLEDStripWrapperService {

    private static final int FRAME_DURATION = 10;
    private static final int CHIP_TYPE = 2812;

    private final BrickService brickService;

    @Autowired
    public BrickletLEDStripWrapperService(BrickService brickService) {

        this.brickService = brickService;
    }

    public BrickletLEDStripWrapper getBrickletLEDStrip(LEDStrip ledStrip) {

        IPConnection ipConnection = brickService.connect(ledStrip.getBrick());

        BrickletLEDStripWrapper brickletLEDStrip = new BrickletLEDStripWrapper(ledStrip.getUid(), ipConnection);

        try {
            brickletLEDStrip.setFrameDuration(FRAME_DURATION);
            brickletLEDStrip.setChipType(CHIP_TYPE);
        } catch (TimeoutException | NotConnectedException exception) {
            throw new LoadFailedException("Error setting up LED strip:", exception);
        }

        return brickletLEDStrip;
    }
}
