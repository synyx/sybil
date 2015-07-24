package org.synyx.sybil.bricklet.input.illuminance;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.bricklet.BrickletProvider;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDomain;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class IlluminanceServiceUnitTest {

    @Mock
    BrickletProvider brickletProviderMock;

    @Mock
    BrickletAmbientLightWrapper brickletAmbientLightWrapperMock;

    IlluminanceService sut;

    @Before
    public void setup() throws Exception {

        when(brickletProviderMock.getBrickletAmbientLight(any(IlluminanceDomain.class))).thenReturn(
            brickletAmbientLightWrapperMock);

        when(brickletAmbientLightWrapperMock.getIlluminance()).thenReturn(100);

        sut = new IlluminanceService(brickletProviderMock);
    }


    @Test
    public void getIlluminance() throws Exception {

        IlluminanceDomain illuminanceDomain = new IlluminanceDomain();
        IlluminanceDTO illuminanceDTO = new IlluminanceDTO(illuminanceDomain);

        int illuminance = sut.getIlluminance(illuminanceDTO);

        InOrder inOrder = inOrder(brickletAmbientLightWrapperMock);

        inOrder.verify(brickletAmbientLightWrapperMock).getIlluminance();
        inOrder.verify(brickletAmbientLightWrapperMock).disconnect();

        assertThat(illuminance, is(100));
    }
}
