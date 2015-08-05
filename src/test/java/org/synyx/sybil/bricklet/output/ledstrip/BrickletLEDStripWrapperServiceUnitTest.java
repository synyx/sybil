package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.IPConnection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.bricklet.output.ledstrip.persistence.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.service.BrickletLEDStripWrapper;
import org.synyx.sybil.bricklet.output.ledstrip.service.BrickletLEDStripWrapperService;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(BrickletLEDStripWrapperService.class)
public class BrickletLEDStripWrapperServiceUnitTest {

    @Mock
    BrickService brickServiceMock;

    @Mock
    LEDStrip ledStripMock;

    @Mock
    IPConnection ipConnectionMock;

    @Mock
    BrickletLEDStripWrapper brickletLEDStripWrapperMock;

    @Test
    public void getBrickletLEDStrip() throws Exception {

        // setup
        when(ledStripMock.getBrick()).thenReturn("brick");

        when(brickServiceMock.connect("brick")).thenReturn(ipConnectionMock);

        whenNew(BrickletLEDStripWrapper.class).withAnyArguments().thenReturn(brickletLEDStripWrapperMock);

        BrickletLEDStripWrapperService sut = new BrickletLEDStripWrapperService(brickServiceMock);

        // execution
        sut.getBrickletLEDStrip(ledStripMock);

        // verification
        InOrder inOrder = inOrder(brickletLEDStripWrapperMock);

        inOrder.verify(brickletLEDStripWrapperMock).setFrameDuration(10);
        inOrder.verify(brickletLEDStripWrapperMock).setChipType(2812);
    }
}
