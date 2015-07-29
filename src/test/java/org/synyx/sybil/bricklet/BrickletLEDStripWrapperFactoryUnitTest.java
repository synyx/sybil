package org.synyx.sybil.bricklet;

import com.tinkerforge.IPConnection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.synyx.sybil.brick.BrickDTOService;
import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.bricklet.output.ledstrip.BrickletLEDStripWrapper;
import org.synyx.sybil.bricklet.output.ledstrip.BrickletLEDStripWrapperFactory;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BrickletLEDStripWrapperFactory.class)
public class BrickletLEDStripWrapperFactoryUnitTest {

    @Mock
    BrickService brickServiceMock;

    @Mock
    BrickDTOService brickDTOServiceMock;

    @Mock
    LEDStripDomain ledStripDomainMock;

    @Mock
    BrickDTO brickDTOMock;

    @Mock
    IPConnection ipConnectionMock;

    @Mock
    BrickletLEDStripWrapper brickletLEDStripWrapperMock;

    @Test
    public void getBrickletLEDStrip() throws Exception {

        // setup
        when(ledStripDomainMock.getBrick()).thenReturn("brick");

        when(brickDTOServiceMock.connect("brick")).thenReturn(ipConnectionMock);

        whenNew(BrickletLEDStripWrapper.class).withAnyArguments().thenReturn(brickletLEDStripWrapperMock);

        BrickletLEDStripWrapperFactory sut = new BrickletLEDStripWrapperFactory(brickDTOServiceMock);

        // execution
        sut.getBrickletLEDStrip(ledStripDomainMock);

        // verification
        InOrder inOrder = inOrder(brickletLEDStripWrapperMock);

        inOrder.verify(brickletLEDStripWrapperMock).setFrameDuration(10);
        inOrder.verify(brickletLEDStripWrapperMock).setChipType(2812);
    }
}
