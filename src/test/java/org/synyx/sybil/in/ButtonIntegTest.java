package org.synyx.sybil.in;

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
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.database.OutputRelayRepository;
import org.synyx.sybil.domain.InputSensorDomain;
import org.synyx.sybil.domain.OutputRelayDomain;
import org.synyx.sybil.out.EnumRelay;
import org.synyx.sybil.out.OutputRelay;
import org.synyx.sybil.out.OutputRelayRegistry;

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
    private OutputRelayRegistry outputRelayRegistry;

    @Autowired
    private OutputRelayRepository outputRelayRepository;

    @Autowired
    private BrickRepository brickRepository;

    @Autowired
    private BrickRegistry brickRegistry;

    private OutputRelay relay;

    private ButtonListener listener;

    @Before
    public void setup() {

        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);

        brickRepository.save(test1);

        OutputRelayDomain testOne = new OutputRelayDomain("testone", "zzz", test1);

        testOne = outputRelayRepository.save(testOne);

        relay = outputRelayRegistry.get(testOne);

        relay.setStates(true, true);

        List<String> outputs = new ArrayList<>();

        outputs.add("testone");

        InputSensorDomain sensorDomain = new InputSensorDomain("button", "egal", SensorType.BUTTON, 16, 0.1, 0,
                (short) 0b0001, outputs, test1);

        listener = new ButtonListener(sensorDomain, outputRelayRegistry, outputRelayRepository);
    }


    @After
    public void close() {

        relay.setStates(false, false);

        OutputRelayDomain outputRelayDomain = outputRelayRepository.findByName(relay.getName());
        brickRepository.delete(outputRelayDomain.getBrickDomain());
        outputRelayRepository.delete(outputRelayDomain);

        brickRegistry.disconnectAll();
    }


    @Test
    public void testIlluminanceSensor() {

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
