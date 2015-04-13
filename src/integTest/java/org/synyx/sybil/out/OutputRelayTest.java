package org.synyx.sybil.out;

import com.tinkerforge.NotConnectedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.common.BrickRegistry;
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputRelayRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputRelayDomain;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class OutputRelayTest {

    private static final Logger LOG = LoggerFactory.getLogger(OutputRelayTest.class);

    private List<OutputRelay> outputRelays = new ArrayList<>();

    @Autowired
    private OutputRelayRegistry outputRelayRegistry;

    @Autowired
    private OutputRelayRepository outputRelayRepository;

    @Autowired
    private BrickRepository brickRepository;

    @Autowired
    private BrickRegistry brickRegistry;

    @Before
    public void setup() {

        // define Bricks
        BrickDomain test1 = new BrickDomain("localhost", 14223);
        BrickDomain test2 = new BrickDomain("localhost", 14224);
        BrickDomain test3 = new BrickDomain("localhost", 14225);

        // add them to the database
        brickRepository.save(test1);
        brickRepository.save(test2);
        brickRepository.save(test3);

        // define LED Strips (bricklets)
        OutputRelayDomain testOne = new OutputRelayDomain("testone", "abc", test1);
        OutputRelayDomain testTwo = new OutputRelayDomain("testtwo", "def", test2);
        OutputRelayDomain testThree = new OutputRelayDomain("testthree", "ghi", test3);

        // add them to the database
        testOne = outputRelayRepository.save(testOne);
        testTwo = outputRelayRepository.save(testTwo);
        testThree = outputRelayRepository.save(testThree);

        // initialise LED Strips (fetching them from the database on the way), cast and add them to the list
        outputRelays.add(outputRelayRegistry.get(testOne));
        outputRelays.add(outputRelayRegistry.get(testTwo));
        outputRelays.add(outputRelayRegistry.get(testThree));

        // set all to false (off)
        for (OutputRelay outputRelay : outputRelays) {
            outputRelay.setStates(false, false);
        }
    }


    @After
    public void close() throws NotConnectedException {

        for (OutputRelay outputRelay : outputRelays) { // iterate over list of strips

            OutputRelayDomain outputRelayDomain = outputRelayRepository.findByName(outputRelay.getName());
            brickRepository.delete(outputRelayDomain.getBrickDomain());
            outputRelayRepository.delete(outputRelayDomain);
        }

        brickRegistry.disconnectAll();
    }


    @Test
    public void testSetState() {

        for (OutputRelay outputRelay : outputRelays) {
            boolean[] states = outputRelay.getStates();
            assertArrayEquals(new boolean[] { false, false }, states);

            outputRelay.setStates(true, true);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            states = outputRelay.getStates();
            assertArrayEquals(new boolean[] { true, true }, states);
        }
    }
}
