package org.synyx.sybil.bricklet.output.ledstrip;

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
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStrip;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BrickletLEDStripWrapperService.class)
public class BrickletLEDStripWrapperServiceUnitTest {

    @Mock
    BrickService brickServiceMock;

    @Mock
    BrickDTOService brickDTOServiceMock;

    @Mock
    LEDStrip ledStripMock;

    @Mock
    BrickDTO brickDTOMock;

    @Mock
    IPConnection ipConnectionMock;

    @Mock
    BrickletLEDStripWrapper brickletLEDStripWrapperMock;

    @Test
    public void getBrickletLEDStrip() throws Exception {

        // setup
        when(ledStripMock.getBrick()).thenReturn("brick");

        when(brickDTOServiceMock.connect("brick")).thenReturn(ipConnectionMock);

        whenNew(BrickletLEDStripWrapper.class).withAnyArguments().thenReturn(brickletLEDStripWrapperMock);

        BrickletLEDStripWrapperService sut = new BrickletLEDStripWrapperService(brickDTOServiceMock);

        // execution
        sut.getBrickletLEDStrip(ledStripMock);

        // verification
        InOrder inOrder = inOrder(brickletLEDStripWrapperMock);

        inOrder.verify(brickletLEDStripWrapperMock).setFrameDuration(10);
        inOrder.verify(brickletLEDStripWrapperMock).setChipType(2812);
    }
}
