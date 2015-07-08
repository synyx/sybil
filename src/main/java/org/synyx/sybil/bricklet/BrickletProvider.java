package org.synyx.sybil.bricklet;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;


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

    @Autowired
    public BrickletProvider(BrickService brickService) {

        this.brickService = brickService;
    }

    public BrickletLEDStrip getBrickletLEDStrip(LEDStripDomain ledStripDomain) throws TimeoutException,
        NotConnectedException {

        IPConnection ipConnection = brickService.getIPConnection(ledStripDomain.getBrick());

        BrickletLEDStrip brickletLEDStrip = new BrickletLEDStrip(ledStripDomain.getUid(), ipConnection);

        brickletLEDStrip.setFrameDuration(FRAME_DURATION);
        brickletLEDStrip.setChipType(CHIP_TYPE);

        return brickletLEDStrip;
    }
}
