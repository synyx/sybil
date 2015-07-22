package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceDTOService;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceService;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDomain;
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
    BrickletProvider brickletProviderMock;

    @Mock
    BrickletLEDStripWrapper brickletLEDStripMock;

    @Mock
    BrickletLEDStrip.RGBValues rgbValuesOneMock;

    @Mock
    BrickletLEDStrip.RGBValues rgbValuesTwoMock;

    @Mock
    IlluminanceDTOService illuminanceDTOServiceMock;

    @Mock
    IlluminanceService illuminanceServiceMock;

    LEDStripService sut;

    @Before
    public void setup() throws Exception {

        when(brickletProviderMock.getBrickletLEDStrip(any(LEDStripDomain.class))).thenReturn(brickletLEDStripMock);

        sut = new LEDStripService(brickletProviderMock, illuminanceDTOServiceMock, illuminanceServiceMock);
    }


    @Test
    public void getBrightness() throws Exception {

        // setup
        // multiplier of 1.0 means every 1 Lux less than threshold increases brightness by a factor of 1
        IlluminanceDomain illuminanceDomain = new IlluminanceDomain("ambientlight", "abc", 16, 1.0, "somebrick");
        IlluminanceDTO illuminanceDTO = new IlluminanceDTO();
        illuminanceDTO.setDomain(illuminanceDomain);

        when(illuminanceDTOServiceMock.getDTO("ambientlight")).thenReturn(illuminanceDTO);

        // 140 decilux is 20 less than the configured threshold of 16 lux, so brigthness should triple.
        when(illuminanceServiceMock.getIlluminance(illuminanceDTO)).thenReturn(140);

        LEDStripDomain ledStripDomain = new LEDStripDomain("one", "xyz", 16, "abrick", "ambientlight");
        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(ledStripDomain);
        ledStripDTO.setStatus(new StatusInformation("test", Status.OKAY));

        // execution
        sut.handleStatus(ledStripDTO);

        // verification
        short[] zeroes = new short[16];
        short[] green = new short[16];

        // brightness should be tripled
        Arrays.fill(green, (short) (Color.OKAY.getGreen() * 3));

        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, zeroes, zeroes, green);
    }


    @Test
    public void getPixels() throws Exception {

        // setup
        short[] r = new short[16];
        short[] g = new short[16];
        short[] b = new short[16];

        for (int i = 0; i < 16; i++) {
            r[i] = (short) i;
            g[i] = (short) (i * 2);
            b[i] = (short) (i * 3);
        }

        // WS2812 use BRG instead of RGB
        rgbValuesOneMock.r = b.clone();
        rgbValuesOneMock.g = r.clone();
        rgbValuesOneMock.b = g.clone();

        for (int i = 0; i < 16; i++) {
            r[i] = (short) (i + 16);
            g[i] = (short) ((i + 16) * 2);
            b[i] = (short) ((i + 16) * 3);
        }

        // WS2812 use BRG instead of RGB
        rgbValuesTwoMock.r = b.clone();
        rgbValuesTwoMock.g = r.clone();
        rgbValuesTwoMock.b = g.clone();

        when(brickletLEDStripMock.getRGBValues(0, (short) 16)).thenReturn(rgbValuesOneMock);
        when(brickletLEDStripMock.getRGBValues(16, (short) 16)).thenReturn(rgbValuesTwoMock);

        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 30, "abrick"));

        // execution
        List<Color> pixels = sut.getPixels(ledStripDTO);

        // verification
        for (int i = 0; i < 30; i++) {
            assertThat(pixels.get(i), is(new Color(i, i * 2, i * 3)));
        }
    }


    @Test
    public void handleSprite() throws Exception {

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

        InOrder inOrder = Mockito.inOrder(brickletLEDStripMock);

        inOrder.verify(brickletLEDStripMock).setRGBValues(0, (short) 16, allWhite, allWhite, allWhite);
        inOrder.verify(brickletLEDStripMock).setRGBValues(16, (short) 16, partlyWhite, partlyWhite, partlyWhite);
    }


    @Test
    public void handleTooLongSprite() throws Exception {

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 31, "abrick"));

        List<Color> colors = Arrays.asList(new Color[33]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(33));

        Sprite1D sprite = new Sprite1D(colors.size(), colors);
        ledStripDTO.setSprite(sprite);

        // execution
        sut.handleSprite(ledStripDTO);

        // verification
        short[] allWhite = new short[16];
        Arrays.fill(allWhite, (short) 255);

        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, allWhite, allWhite, allWhite);
        verify(brickletLEDStripMock).setRGBValues(16, (short) 16, allWhite, allWhite, allWhite);
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
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
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
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
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
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
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
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
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
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
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
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
    }


    @Test
    public void turnOff() throws Exception {

        // setup
        LEDStripDTO ledStripDTO = new LEDStripDTO();
        ledStripDTO.setDomain(new LEDStripDomain("one", "abc", 16, "abrick"));

        // execution
        sut.turnOff(ledStripDTO);

        // verification
        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
    }
}
