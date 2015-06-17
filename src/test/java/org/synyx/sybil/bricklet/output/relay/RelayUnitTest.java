package org.synyx.sybil.bricklet.output.relay;

import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


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
    public void setState() throws Exception {

        relay.setState(EnumRelay.ONE, true);

        verify(brickletDualRelay).setSelectedState((short) 1, true);
    }


    @Test
    public void setStates() throws TimeoutException, NotConnectedException {

        relay.setStates(false, true);

        verify(brickletDualRelay).setState(false, true);
    }
}
