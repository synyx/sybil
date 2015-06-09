package org.synyx.sybil.bricklet.input.button;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;
import org.synyx.sybil.bricklet.output.relay.Relay;
import org.synyx.sybil.bricklet.output.relay.RelayRegistry;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;
import org.synyx.sybil.bricklet.output.relay.database.RelayRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * ButtonTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class ButtonUnitTest {

    @Mock
    private RelayRegistry relayRegistry;

    @Mock
    private RelayRepository relayRepository;

    @Mock
    private Relay relay;

    private ButtonListener listener;

    @Before
    public void setup() {

        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);

        RelayDomain testOne = new RelayDomain("testone", "zzz", test1);

        Mockito.when(relayRepository.findByName("testone")).thenReturn(testOne);

        Mockito.when(relayRegistry.get(testOne)).thenReturn(relay);

        List<String> outputs = new ArrayList<>();

        outputs.add("testone");

        ButtonDomain sensorDomain = new ButtonDomain("button", "egal", (short) 0b0001, outputs, test1);

        listener = new ButtonListener(sensorDomain, relayRegistry, relayRepository);
    }


    @Test
    public void testButton() {

        // These shouldn't do anything
        listener.interrupt((short) 0b1111, (short) 0b1111); // all interrupts are triggered, all inputs are high

        Mockito.verifyNoMoreInteractions(relay);

        listener.interrupt((short) 0b0000, (short) 0b0000); // no interrupts are triggered, all inputs are low

        Mockito.verifyNoMoreInteractions(relay);

        listener.interrupt((short) 0b1110, (short) 0b0000); // all interrupts but the configured one are triggered, all inputs are low

        Mockito.verifyNoMoreInteractions(relay);

        // Now something should happen!
        listener.interrupt((short) 0b0001, (short) 0b1110); // the configured interrupt is triggered and the configured input is low

        Mockito.verify(relay).setStates(false, false);
    }
}
