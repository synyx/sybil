package org.synyx.sybil.bricklet.input.illuminance;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class IlluminanceServiceUnitTest {

    @Mock
    BrickletAmbientLightWrapperFactory brickletAmbientLightWrapperFactory;

    @Mock
    BrickletAmbientLightWrapper brickletAmbientLightWrapperMock;

    IlluminanceService sut;

    @Before
    public void setup() throws Exception {

        when(brickletAmbientLightWrapperFactory.getBrickletAmbientLight(any(IlluminanceConfig.class))).thenReturn(
            brickletAmbientLightWrapperMock);

        when(brickletAmbientLightWrapperMock.getIlluminance()).thenReturn(100);

        sut = new IlluminanceService(brickletAmbientLightWrapperFactory);
    }


    @Test
    public void getIlluminance() throws Exception {

        IlluminanceConfig illuminanceConfig = new IlluminanceConfig();
        IlluminanceDTO illuminanceDTO = new IlluminanceDTO(illuminanceConfig);

        int illuminance = sut.getIlluminance(illuminanceDTO);

        InOrder inOrder = inOrder(brickletAmbientLightWrapperMock);

        inOrder.verify(brickletAmbientLightWrapperMock).getIlluminance();
        inOrder.verify(brickletAmbientLightWrapperMock).disconnect();

        assertThat(illuminance, is(100));
    }
}
