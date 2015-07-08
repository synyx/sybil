package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

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

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 20, "abrick"));

        List<Color> colors = Arrays.asList(new Color[20]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(20));

        Sprite1D sprite = new Sprite1D(20, "test", colors);
        ledStripDTO.setSprite(sprite);

        // execution
        sut.handleSprite(ledStripDTO);

        // verification
        short[] white = new short[16];
        Arrays.fill(white, (short) 255);

        short[] partlyWhite = new short[16];

        for (int i = 0; i < 4; i++) {
            partlyWhite[i] = (short) 255;
        }

        InOrder inOrder = Mockito.inOrder(brickletLEDStrip);

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, white, white, white);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, partlyWhite, partlyWhite, partlyWhite);
    }
}
