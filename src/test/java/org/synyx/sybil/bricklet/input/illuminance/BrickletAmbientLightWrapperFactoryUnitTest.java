package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.IPConnection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BrickletAmbientLightWrapperFactory.class)
public class BrickletAmbientLightWrapperFactoryUnitTest {

    @Mock
    BrickService brickServiceMock;

    @Mock
    BrickDTOService brickDTOServiceMock;

    @Mock
    IlluminanceConfig illuminanceConfigMock;

    @Mock
    BrickDTO brickDTOMock;

    @Mock
    IPConnection ipConnectionMock;

    @Mock
    BrickletAmbientLightWrapper brickletAmbientLightWrapperMock;

    BrickletAmbientLightWrapperFactory sut;

    @Test
    public void getBrickletAmbientLightWrapper() throws Exception {

        // setup
        when(illuminanceConfigMock.getBrick()).thenReturn("brick");

        when(brickDTOServiceMock.connect("brick")).thenReturn(ipConnectionMock);

        whenNew(BrickletAmbientLightWrapper.class).withAnyArguments().thenReturn(brickletAmbientLightWrapperMock);

        sut = new BrickletAmbientLightWrapperFactory(brickDTOServiceMock);

        // execution
        BrickletAmbientLightWrapper brickletAmbientLightWrapper = sut.getBrickletAmbientLight(illuminanceConfigMock);

        // verification
        assertThat(brickletAmbientLightWrapper, is(brickletAmbientLightWrapperMock));
    }
}
