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

import org.synyx.sybil.bricklet.input.illuminance.IlluminanceConnectionException;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceDTOService;
import org.synyx.sybil.bricklet.input.illuminance.IlluminanceService;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStrip;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.lang.reflect.Constructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doThrow;
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
    BrickletLEDStripWrapperService brickletLEDStripWrapperServiceMock;

    @Mock
    LEDStripRepository ledStripRepository;

    @Mock
    IlluminanceDTOService illuminanceDTOServiceMock;

    @Mock
    IlluminanceService illuminanceServiceMock;

    @Mock
    BrickletLEDStripWrapper brickletLEDStripMock;

    @Mock
    BrickletLEDStrip.RGBValues rgbValuesOneMock;

    @Mock
    BrickletLEDStrip.RGBValues rgbValuesTwoMock;

    LEDStripService sut;

    @Before
    public void setup() throws Exception {

        when(brickletLEDStripWrapperServiceMock.getBrickletLEDStrip(any(LEDStrip.class))).thenReturn(
            brickletLEDStripMock);

        sut = new LEDStripService(brickletLEDStripWrapperServiceMock, illuminanceDTOServiceMock, illuminanceServiceMock,
                ledStripRepository);
    }


    @Test
    public void getBrightnessTripled() throws Exception {

        // setup
        // multiplier of 1.0 means every 1 Lux less than threshold increases brightness by a factor of 1
        IlluminanceConfig illuminanceConfig = new IlluminanceConfig("ambientlight", "abc", 16, 1.0, "somebrick");
        IlluminanceDTO illuminanceDTO = new IlluminanceDTO(illuminanceConfig);

        when(illuminanceDTOServiceMock.getDTO("ambientlight")).thenReturn(illuminanceDTO);

        // 140 decilux is 20 less than the configured threshold of 16 lux, so brigthness should triple.
        when(illuminanceServiceMock.getIlluminance(illuminanceDTO)).thenReturn(140);

        LEDStrip ledStrip = new LEDStrip("one", "xyz", 16, "abrick", "ambientlight");
        when(ledStripRepository.get("one")).thenReturn(ledStrip);

        // execution
        sut.handleStatus("one", new StatusInformation("test", Status.OKAY));

        // verification
        short[] zeroes = new short[16];
        short[] green = new short[16];

        // brightness should be tripled
        Arrays.fill(green, (short) (Color.OKAY.getGreen() * 3));

        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, zeroes, zeroes, green);
    }


    @Test
    public void getBrightnessMax() throws Exception {

        // setup
        // multiplier of 1.0 means every 1 Lux less than threshold increases brightness by a factor of 1
        IlluminanceConfig illuminanceConfig = new IlluminanceConfig("ambientlight", "abc", 16, 1.0, "somebrick");
        IlluminanceDTO illuminanceDTO = new IlluminanceDTO(illuminanceConfig);

        when(illuminanceDTOServiceMock.getDTO("ambientlight")).thenReturn(illuminanceDTO);

        // 0 decilux is absolute darkness and should result in maximal brightness.
        when(illuminanceServiceMock.getIlluminance(illuminanceDTO)).thenReturn(0);

        LEDStrip ledStrip = new LEDStrip("one", "xyz", 16, "abrick", "ambientlight");
        when(ledStripRepository.get("one")).thenReturn(ledStrip);

        // execution
        sut.handleStatus("one", new StatusInformation("test", Status.OKAY));

        // verification
        short[] zeroes = new short[16];
        short[] green = new short[16];

        // brightness should be tripled
        Arrays.fill(green, (short) 255);

        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, zeroes, zeroes, green);
    }


    @Test
    public void getBrightnessMin() throws Exception {

        // setup
        // multiplier of 1.0 means every 1 Lux less than threshold increases brightness by a factor of 1
        IlluminanceConfig illuminanceConfig = new IlluminanceConfig("ambientlight", "abc", 16, 1.0, "somebrick");
        IlluminanceDTO illuminanceDTO = new IlluminanceDTO(illuminanceConfig);

        when(illuminanceDTOServiceMock.getDTO("ambientlight")).thenReturn(illuminanceDTO);

        // 5000 lux is over the threshold, so the brightness should not change from default.
        when(illuminanceServiceMock.getIlluminance(illuminanceDTO)).thenReturn(5000);

        LEDStrip ledStrip = new LEDStrip("one", "xyz", 16, "abrick", "ambientlight");
        when(ledStripRepository.get("one")).thenReturn(ledStrip);

        // execution
        sut.handleStatus("one", new StatusInformation("test", Status.OKAY));

        // verification
        short[] zeroes = new short[16];
        short[] green = new short[16];

        // brightness should be tripled
        Arrays.fill(green, (short) Color.OKAY.getGreen());

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

        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        // execution
        List<Color> pixels = sut.getPixels("one");

        // verification
        for (int i = 0; i < 30; i++) {
            assertThat(pixels.get(i), is(new Color(i, i * 2, i * 3)));
        }
    }


    @Test
    public void handleSprite() throws Exception {

        // setup
        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        List<Color> colors = Arrays.asList(new Color[20]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(20));

        Sprite1D sprite = new Sprite1D(colors);

        // execution
        sut.handleSprite("one", sprite);

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
        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        List<Color> colors = Arrays.asList(new Color[33]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(33));

        Sprite1D sprite = new Sprite1D(colors);

        // execution
        sut.handleSprite("one", sprite);

        // verification
        short[] allWhite = new short[16];
        Arrays.fill(allWhite, (short) 255);

        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, allWhite, allWhite, allWhite);
        verify(brickletLEDStripMock).setRGBValues(16, (short) 16, allWhite, allWhite, allWhite);
    }


    @Test
    public void handleTooShortSprite() throws Exception {

        // setup
        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        List<Color> colors = Arrays.asList(new Color[15]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(15));

        Sprite1D sprite = new Sprite1D(colors);

        // execution
        sut.handleSprite("one", sprite);

        // verification
        short[] partlyWhite = new short[16];

        for (int i = 0; i < 15; i++) {
            partlyWhite[i] = (short) 255;
        }

        short[] black = new short[16];

        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, partlyWhite, partlyWhite, partlyWhite);
        verify(brickletLEDStripMock).setRGBValues(16, (short) 16, black, black, black);
    }


    @Test
    public void handleStatusOkay() throws Exception {

        // setup
        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        StatusInformation status = new StatusInformation("test", Status.OKAY);

        // execution
        sut.handleStatus("one", status);

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
        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        StatusInformation status = new StatusInformation("test", Status.WARNING);

        // execution
        sut.handleStatus("one", status);

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
        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        StatusInformation status = new StatusInformation("test", Status.CRITICAL);

        // execution
        sut.handleStatus("one", status);

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
        LEDStrip ledStrip = new LEDStrip("one", "abc", 16, "abrick");

        ledStrip.setOkayRed(16);
        ledStrip.setOkayGreen(16);
        ledStrip.setOkayBlue(16);

        ledStrip.setWarningRed(32);
        ledStrip.setWarningGreen(32);
        ledStrip.setWarningBlue(0);

        ledStrip.setCriticalRed(0);
        ledStrip.setCriticalGreen(0);
        ledStrip.setCriticalBlue(255);

        when(ledStripRepository.get("one")).thenReturn(ledStrip);

        StatusInformation status = new StatusInformation("test", Status.OKAY);

        // execution
        sut.handleStatus("one", status);

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
        LEDStrip ledStrip = new LEDStrip("one", "abc", 16, "abrick");

        ledStrip.setOkayRed(16);
        ledStrip.setOkayGreen(16);
        ledStrip.setOkayBlue(16);

        ledStrip.setWarningRed(32);
        ledStrip.setWarningGreen(32);
        ledStrip.setWarningBlue(0);

        ledStrip.setCriticalRed(0);
        ledStrip.setCriticalGreen(0);
        ledStrip.setCriticalBlue(255);

        when(ledStripRepository.get("one")).thenReturn(ledStrip);

        StatusInformation status = new StatusInformation("test", Status.WARNING);

        // execution
        sut.handleStatus("one", status);

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
        LEDStrip ledStrip = new LEDStrip("one", "abc", 16, "abrick");

        ledStrip.setOkayRed(16);
        ledStrip.setOkayGreen(16);
        ledStrip.setOkayBlue(16);

        ledStrip.setWarningRed(32);
        ledStrip.setWarningGreen(32);
        ledStrip.setWarningBlue(0);

        ledStrip.setCriticalRed(0);
        ledStrip.setCriticalGreen(0);
        ledStrip.setCriticalBlue(9000);

        when(ledStripRepository.get("one")).thenReturn(ledStrip);

        StatusInformation status = new StatusInformation("test", Status.CRITICAL);

        // execution
        sut.handleStatus("one", status);

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
    public void turnOffAllLEDStrips() throws Exception {

        // setup
        List<LEDStrip> ledStrips = Arrays.asList(new LEDStrip("one", "abc", 16, "abrick"));

        when(ledStripRepository.getAll()).thenReturn(ledStrips);

        // execution
        sut.turnOffAllLEDStrips();

        // verification
        short[] red = new short[16];
        short[] green = new short[16];
        short[] blue = new short[16];

        // The LED chips expect data in BRG, not RGB
        verify(brickletLEDStripMock).setRGBValues(0, (short) 16, blue, red, green);
    }


    @Test(expected = LEDStripNotFoundException.class)
    public void handleStatusWithNonexistentLEDStrip() {

        sut.handleStatus("noledstrip", new StatusInformation("Test", Status.OKAY));
    }


    @Test(expected = LEDStripNotFoundException.class)
    public void handleSpriteWithNonexistentLEDStrip() {

        sut.handleSprite("noledstrip", new Sprite1D(1));
    }


    @Test(expected = LEDStripConnectionException.class)
    public void getPixelsWithTimeoutException() throws Exception {

        // setup
        // set up exception through reflection
        Constructor<TimeoutException> constructor;
        constructor = TimeoutException.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        TimeoutException exception = constructor.newInstance();

        when(brickletLEDStripMock.getRGBValues(anyInt(), anyShort())).thenThrow(exception);

        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        // execution
        sut.getPixels("one");
    }


    @Test(expected = LEDStripConnectionException.class)
    public void getPixelsWithNotConnectedException() throws Exception {

        // setup
        // set up exception through reflection
        Constructor<NotConnectedException> constructor;
        constructor = NotConnectedException.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        NotConnectedException exception = constructor.newInstance();

        when(brickletLEDStripMock.getRGBValues(anyInt(), anyShort())).thenThrow(exception);

        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        // execution
        sut.getPixels("one");
    }


    @Test(expected = LEDStripConnectionException.class)
    public void handleSpriteWithTimeoutException() throws Exception {

        // setup
        // set up exception through reflection
        Constructor<TimeoutException> constructor;
        constructor = TimeoutException.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        TimeoutException exception = constructor.newInstance();

        short[] white = new short[16];
        Arrays.fill(white, (short) 255);

        doThrow(exception).when(brickletLEDStripMock)
            .setRGBValues(anyInt(), eq((short) 16), eq(white), eq(white), eq(white));

        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        List<Color> colors = Arrays.asList(new Color[20]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(20));

        Sprite1D sprite = new Sprite1D(colors);

        // execution
        sut.handleSprite("one", sprite);
    }


    @Test(expected = LEDStripConnectionException.class)
    public void handleSpriteWithNotConnectedException() throws Exception {

        // setup
        // set up exception through reflection
        Constructor<NotConnectedException> constructor;
        constructor = NotConnectedException.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        NotConnectedException exception = constructor.newInstance();

        short[] white = new short[16];
        Arrays.fill(white, (short) 255);

        doThrow(exception).when(brickletLEDStripMock)
            .setRGBValues(anyInt(), eq((short) 16), eq(white), eq(white), eq(white));

        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick"));

        List<Color> colors = Arrays.asList(new Color[20]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(20));

        Sprite1D sprite = new Sprite1D(colors);

        // execution
        sut.handleSprite("one", sprite);
    }


    @Test(expected = LEDStripConnectionException.class)
    public void getIlluminanceWithException() {

        // setup
        doThrow(new IlluminanceConnectionException("")).when(illuminanceServiceMock).getIlluminance(any());

        List<Color> colors = Arrays.asList(new Color[20]);
        Collections.fill(colors, Color.WHITE);

        assertThat(colors.size(), is(20));

        Sprite1D sprite = new Sprite1D(colors);

        when(ledStripRepository.get("one")).thenReturn(new LEDStrip("one", "abc", 30, "abrick", "somesensor"));
        when(illuminanceDTOServiceMock.getDTO("somesensor")).thenReturn(new IlluminanceDTO(new IlluminanceConfig()));

        // execution
        sut.handleSprite("one", sprite);
    }
}
