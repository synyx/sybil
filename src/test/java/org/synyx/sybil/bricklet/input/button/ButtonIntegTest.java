package org.synyx.sybil.bricklet.input.button;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.brick.BrickRegistry;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;
import org.synyx.sybil.bricklet.output.relay.EnumRelay;
import org.synyx.sybil.bricklet.output.relay.Relay;
import org.synyx.sybil.bricklet.output.relay.RelayRegistry;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;
import org.synyx.sybil.bricklet.output.relay.database.RelayRepository;
import org.synyx.sybil.config.DevSpringConfig;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


/**
 * ButtonTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class ButtonIntegTest {

    @Autowired
    private RelayRegistry outputRelayRegistry;

    @Autowired
    private RelayRepository outputRelayRepository;

    @Autowired
    private BrickRepository brickRepository;

    @Autowired
    private BrickRegistry brickRegistry;

    private Relay relay;

    private ButtonListener listener;

    @Before
    public void setup() {

        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);

        brickRepository.save(test1);

        RelayDomain testOne = new RelayDomain("testone", "zzz", test1);

        testOne = outputRelayRepository.save(testOne);

        relay = outputRelayRegistry.get(testOne);

        relay.setStates(true, true);

        List<String> outputs = new ArrayList<>();

        outputs.add("testone");

        ButtonDomain sensorDomain = new ButtonDomain("button", "egal", (short) 0b0001, outputs, test1);

        listener = new ButtonListener(sensorDomain, outputRelayRegistry, outputRelayRepository);
    }


    @After
    public void close() {

        relay.setStates(false, false);

        RelayDomain outputRelayDomain = outputRelayRepository.findByName(relay.getName());
        brickRepository.delete(outputRelayDomain.getBrickDomain());
        outputRelayRepository.delete(outputRelayDomain);

        brickRegistry.disconnectAll();
    }


    @Test
    public void testButton() {

        // Control
        assertThat(relay.getState(EnumRelay.ONE), is(true));
        assertThat(relay.getState(EnumRelay.TWO), is(true));

        // These shouldn't do anything
        listener.interrupt((short) 0b1111, (short) 0b1111); // all interrupts are triggered, all inputs are high

        assertThat(relay.getState(EnumRelay.ONE), is(true));
        assertThat(relay.getState(EnumRelay.TWO), is(true));

        listener.interrupt((short) 0b0000, (short) 0b0000); // no interrupts are triggered, all inputs are low

        assertThat(relay.getState(EnumRelay.ONE), is(true));
        assertThat(relay.getState(EnumRelay.TWO), is(true));

        listener.interrupt((short) 0b1110, (short) 0b0000); // all interrupts but the configured one are triggered, all inputs are low

        assertThat(relay.getState(EnumRelay.ONE), is(true));
        assertThat(relay.getState(EnumRelay.TWO), is(true));

        // Now something should happen!
        listener.interrupt((short) 0b0001, (short) 0b1110); // the configured interrupt is triggered and the configured input is low

        assertThat(relay.getState(EnumRelay.ONE), is(false));
        assertThat(relay.getState(EnumRelay.TWO), is(false));
    }
}
