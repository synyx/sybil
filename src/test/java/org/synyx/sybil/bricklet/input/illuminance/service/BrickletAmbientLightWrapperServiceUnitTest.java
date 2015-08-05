package org.synyx.sybil.bricklet.input.illuminance.service;

import com.tinkerforge.IPConnection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.synyx.sybil.brick.service.BrickService;
import org.synyx.sybil.bricklet.input.illuminance.persistence.Illuminance;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BrickletAmbientLightWrapperService.class)
public class BrickletAmbientLightWrapperServiceUnitTest {

    @Mock
    BrickService brickServiceMock;

    @Mock
    Illuminance illuminanceMock;

    @Mock
    IPConnection ipConnectionMock;

    @Mock
    BrickletAmbientLightWrapper brickletAmbientLightWrapperMock;

    BrickletAmbientLightWrapperService sut;

    @Test
    public void getBrickletAmbientLightWrapper() throws Exception {

        // setup
        when(illuminanceMock.getBrick()).thenReturn("brick");

        when(brickServiceMock.connect("brick")).thenReturn(ipConnectionMock);

        whenNew(BrickletAmbientLightWrapper.class).withAnyArguments().thenReturn(brickletAmbientLightWrapperMock);

        sut = new BrickletAmbientLightWrapperService(brickServiceMock);

        // execution
        BrickletAmbientLightWrapper brickletAmbientLightWrapper = sut.getBrickletAmbientLight(illuminanceMock);

        // verification
        assertThat(brickletAmbientLightWrapper, is(brickletAmbientLightWrapperMock));
    }
}
