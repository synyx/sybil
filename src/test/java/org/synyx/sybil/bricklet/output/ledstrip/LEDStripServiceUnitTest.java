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
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.verify;
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

        Sprite1D sprite = new Sprite1D(20, colors);
        ledStripDTO.setSprite(sprite);

        // execution
        sut.handleSprite(ledStripDTO);

        // verification
        short[] allWhite = new short[16];
        Arrays.fill(allWhite, (short) 255);

        short[] partlyWhite = new short[16];

        for (int i = 0; i < 4; i++) {
            partlyWhite[i] = (short) 255;
        }

        InOrder inOrder = Mockito.inOrder(brickletLEDStrip);

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, allWhite, allWhite, allWhite);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, partlyWhite, partlyWhite, partlyWhite);
    }


    @Test
    public void handleStatusOkay() throws Exception {

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 16, "abrick"));

        StatusInformation status = new StatusInformation("test", Status.OKAY);
        ledStripDTO.setStatus(status);

        // execution
        sut.handleStatus(ledStripDTO);

        // verification
        Color color = Color.OKAY;

        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        Arrays.fill(red, (short) color.getRed());
        Arrays.fill(green, (short) color.getGreen());
        Arrays.fill(blue, (short) color.getBlue());

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
    }


    @Test
    public void handleStatusWarning() throws Exception {

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 16, "abrick"));

        StatusInformation status = new StatusInformation("test", Status.WARNING);
        ledStripDTO.setStatus(status);

        // execution
        sut.handleStatus(ledStripDTO);

        // verification
        Color color = Color.WARNING;

        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        Arrays.fill(red, (short) color.getRed());
        Arrays.fill(green, (short) color.getGreen());
        Arrays.fill(blue, (short) color.getBlue());

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
    }


    @Test
    public void handleStatusCritical() throws Exception {

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 16, "abrick"));

        StatusInformation status = new StatusInformation("test", Status.CRITICAL);
        ledStripDTO.setStatus(status);

        // execution
        sut.handleStatus(ledStripDTO);

        // verification
        Color color = Color.CRITICAL;

        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        Arrays.fill(red, (short) color.getRed());
        Arrays.fill(green, (short) color.getGreen());
        Arrays.fill(blue, (short) color.getBlue());

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
    }


    @Test
    public void handleStatusCustomOkay() throws Exception {

        // setup
        LEDStripDomain ledStripDomain = new LEDStripDomain("one", "abc", 16, "abrick");

        ledStripDomain.setOkayRed(16);
        ledStripDomain.setOkayGreen(16);
        ledStripDomain.setOkayBlue(16);

        ledStripDomain.setWarningRed(32);
        ledStripDomain.setWarningGreen(32);
        ledStripDomain.setWarningBlue(0);

        ledStripDomain.setCriticalRed(0);
        ledStripDomain.setCriticalGreen(0);
        ledStripDomain.setCriticalBlue(255);

        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(ledStripDomain);

        StatusInformation status = new StatusInformation("test", Status.OKAY);
        ledStripDTO.setStatus(status);

        // execution
        sut.handleStatus(ledStripDTO);

        // verification
        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        Arrays.fill(red, (short) 16);
        Arrays.fill(green, (short) 16);
        Arrays.fill(blue, (short) 16);

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
    }


    @Test
    public void handleStatusCustomWarning() throws Exception {

        // setup
        LEDStripDomain ledStripDomain = new LEDStripDomain("one", "abc", 16, "abrick");

        ledStripDomain.setOkayRed(16);
        ledStripDomain.setOkayGreen(16);
        ledStripDomain.setOkayBlue(16);

        ledStripDomain.setWarningRed(32);
        ledStripDomain.setWarningGreen(32);
        ledStripDomain.setWarningBlue(0);

        ledStripDomain.setCriticalRed(0);
        ledStripDomain.setCriticalGreen(0);
        ledStripDomain.setCriticalBlue(255);

        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(ledStripDomain);

        StatusInformation status = new StatusInformation("test", Status.WARNING);
        ledStripDTO.setStatus(status);

        // execution
        sut.handleStatus(ledStripDTO);

        // verification
        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        Arrays.fill(red, (short) 32);
        Arrays.fill(green, (short) 32);
        Arrays.fill(blue, (short) 0);

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
    }


    @Test
    public void handleStatusCustomCritical() throws Exception {

        // setup
        LEDStripDomain ledStripDomain = new LEDStripDomain("one", "abc", 16, "abrick");

        ledStripDomain.setOkayRed(16);
        ledStripDomain.setOkayGreen(16);
        ledStripDomain.setOkayBlue(16);

        ledStripDomain.setWarningRed(32);
        ledStripDomain.setWarningGreen(32);
        ledStripDomain.setWarningBlue(0);

        ledStripDomain.setCriticalRed(0);
        ledStripDomain.setCriticalGreen(0);
        ledStripDomain.setCriticalBlue(9000);

        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(ledStripDomain);

        StatusInformation status = new StatusInformation("test", Status.CRITICAL);
        ledStripDTO.setStatus(status);

        // execution
        sut.handleStatus(ledStripDTO);

        // verification
        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        Arrays.fill(red, (short) 0);
        Arrays.fill(green, (short) 0);
        Arrays.fill(blue, (short) 255);

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
    }
}
