package org.synyx.sybil.bricklet.output.relay;

import com.tinkerforge.BrickletDualRelay;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RelayUnitTest {

    @Mock
    private BrickletDualRelay brickletDualRelay;

    private Relay relay;

    @Before
    public void setup() {

        relay = new Relay(brickletDualRelay, "UnitTest");
    }


    @Test
    public void testRelay() throws Exception {

        relay.setState(EnumRelay.ONE, true);

        Mockito.verify(brickletDualRelay).setSelectedState((short) 1, true);

        relay.setState(EnumRelay.TWO, true);

        Mockito.verify(brickletDualRelay).setSelectedState((short) 2, true);

        relay.setStates(false, true);

        Mockito.verify(brickletDualRelay).setState(false, true);
    }
}
