package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.when;


/**
 * LEDStripServiceUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class LEDStripServiceUnitTest {

    @Mock
    BrickletProvider brickletProvider;

    @Mock
    BrickletLEDStrip brickletLEDStrip;

    LEDStripService sut;

    @Before
    public void setup() throws Exception {

        when(brickletProvider.getBrickletLEDStrip(any(LEDStripDomain.class))).thenReturn(brickletLEDStrip);

        sut = new LEDStripService(brickletProvider);
    }


    @Test
    public void handleSprite() throws TimeoutException, NotConnectedException {

        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 20, "abrick"));

        List<Color> colors = new ArrayList<>();
        // TODO: Add colors

        Sprite1D sprite = new Sprite1D(20, "test", colors);
        ledStripDTO.setSprite(sprite);

        sut.handleSprite(ledStripDTO);
        // TODO: Verify interactions
    }
}
