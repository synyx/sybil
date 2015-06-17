package org.synyx.sybil.bricklet.input.button;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;
import org.synyx.sybil.bricklet.output.relay.Relay;
import org.synyx.sybil.bricklet.output.relay.RelayService;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


/**
 * ButtonListenerTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class ButtonListenerUnitTest {

    private ButtonListener sut;

    @Mock
    private RelayService relayServiceMock;

    @Mock
    private Relay relayMock;

    @Before
    public void setup() {

        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);

        RelayDomain testOne = new RelayDomain("testone", "zzz", test1);

        when(relayServiceMock.getDomain("testone")).thenReturn(testOne);

        when(relayServiceMock.getRelay(testOne)).thenReturn(relayMock);

        List<String> outputs = new ArrayList<>();

        outputs.add("testone");

        ButtonDomain sensorDomain = new ButtonDomain("button", "egal", (short) 0b0001, outputs, test1);

        sut = new ButtonListener(sensorDomain, relayServiceMock);
    }


    @Test
    public void interruptConfiguredTriggeredConfiguredLow() {

        sut.interrupt((short) 0b0001, (short) 0b1110); // the configured interrupt is triggered and the configured input is low

        verify(relayMock).setStates(false, false);
    }


    @Test
    public void interruptAllTriggeredButConfiguredAllLow() {

        sut.interrupt((short) 0b1110, (short) 0b0000); // all interrupts but the configured one are triggered, all inputs are low

        verifyNoMoreInteractions(relayMock);
    }


    @Test
    public void interruptNoneTriggeredAllLow() {

        sut.interrupt((short) 0b0000, (short) 0b0000); // no interrupts are triggered, all inputs are low

        verifyNoMoreInteractions(relayMock);
    }


    @Test
    public void interruptAllTriggeredAllHigh() {

        sut.interrupt((short) 0b1111, (short) 0b1111); // all interrupts are triggered, all inputs are high

        verifyNoMoreInteractions(relayMock);
    }
}
