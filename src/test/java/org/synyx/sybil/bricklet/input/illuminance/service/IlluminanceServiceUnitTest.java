package org.synyx.sybil.bricklet.input.illuminance.service;

import com.tinkerforge.NotConnectedException;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.bricklet.input.illuminance.persistence.Illuminance;
import org.synyx.sybil.bricklet.input.illuminance.persistence.IlluminanceRepository;

import java.lang.reflect.Constructor;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class IlluminanceServiceUnitTest {

    @Mock
    BrickletAmbientLightWrapperService brickletAmbientLightWrapperService;

    @Mock
    IlluminanceRepository illuminanceRepository;

    @Mock
    BrickletAmbientLightWrapper brickletAmbientLightWrapperMock;

    IlluminanceService sut;

    @Before
    public void setup() throws Exception {

        // multiplier of 1.0 means every 1 Lux less than threshold increases brightness by a factor of 1
        Illuminance illuminance = new Illuminance("ambientlight", "abc", 16, 1.0, "somebrick");

        when(brickletAmbientLightWrapperService.getBrickletAmbientLight(illuminance)).thenReturn(
            brickletAmbientLightWrapperMock);

        when(brickletAmbientLightWrapperMock.getIlluminance()).thenReturn(100);

        when(illuminanceRepository.get("ambientlight")).thenReturn(illuminance);

        sut = new IlluminanceService(brickletAmbientLightWrapperService, illuminanceRepository);
    }


    @Test
    public void getBrightnessTripled() throws Exception {

        // setup
        // 140 decilux is 20 less than the configured threshold of 16 lux, so brigthness should triple.
        when(brickletAmbientLightWrapperMock.getIlluminance()).thenReturn(140);

        // execution
        double brightness = sut.getBrightness("ambientlight");

        // verification
        assertThat(brightness, is(3.0));
    }


    @Test
    public void getBrightnessMax() throws Exception {

        // setup
        // 0 decilux is complete darkness, so it should return a brightness of 1.0 + (threshold * multiplier)
        when(brickletAmbientLightWrapperMock.getIlluminance()).thenReturn(0);

        // execution
        double brightness = sut.getBrightness("ambientlight");

        // verification
        assertThat(brightness, is(17.0));
    }


    @Test
    public void getBrightnessMin() throws Exception {

        // setup
        // 200 decilux is more than the configured threshold so it should return 1.0
        when(brickletAmbientLightWrapperMock.getIlluminance()).thenReturn(200);

        // execution
        double brightness = sut.getBrightness("ambientlight");

        // verification
        assertThat(brightness, is(1.0));
    }


    @Test(expected = IlluminanceNotFoundException.class)
    public void getBrightnessNonExistentSensor() {

        sut.getBrightness("does_not_exist");
    }


    @Test(expected = IlluminanceConnectionException.class)
    public void getBrightnessWithNotConnectedException() throws Exception {

        // setup
        // set up exception through reflection
        Constructor<NotConnectedException> constructor;
        constructor = NotConnectedException.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        NotConnectedException exception = constructor.newInstance();

        when(brickletAmbientLightWrapperMock.getIlluminance()).thenThrow(exception);

        sut.getBrightness("ambientlight");
    }
}
