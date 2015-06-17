package org.synyx.sybil.bricklet.output.relay;

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

import org.synyx.sybil.brick.BrickService;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;
import org.synyx.sybil.bricklet.output.relay.database.RelayRepository;
import org.synyx.sybil.config.DevSpringConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class RelayIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(RelayIntegTest.class);

    private List<Relay> outputRelays = new ArrayList<>();

    @Autowired
    private RelayService outputRelayService;

    @Autowired
    private RelayRepository outputRelayRepository;

    @Autowired
    private BrickService brickService;

    @Before
    public void setup() {

        // define Bricks
        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);
        BrickDomain test2 = new BrickDomain("localhost", "im666", 14224);
        BrickDomain test3 = new BrickDomain("localhost", "123abc", 14225);

        // add them to the database
        brickService.saveDomain(test1);
        brickService.saveDomain(test2);
        brickService.saveDomain(test3);

        // define relay bricklets
        RelayDomain testOne = new RelayDomain("testone", "zzz", test1);
        RelayDomain testTwo = new RelayDomain("testtwo", "yyy", test2);
        RelayDomain testThree = new RelayDomain("testthree", "xxx", test3);

        // add them to the database
        testOne = outputRelayRepository.save(testOne);
        testTwo = outputRelayRepository.save(testTwo);
        testThree = outputRelayRepository.save(testThree);

        // initialise relay bricklets (fetching them from the database on the way), and add them to the list
        outputRelays.add(outputRelayService.getRelay(testOne));
        outputRelays.add(outputRelayService.getRelay(testTwo));
        outputRelays.add(outputRelayService.getRelay(testThree));

        // set all to false (off)
        for (Relay outputRelay : outputRelays) {
            outputRelay.setStates(false, false);
        }
    }


    @After
    public void close() throws NotConnectedException {

        for (Relay outputRelay : outputRelays) { // iterate over list of strips

            RelayDomain outputRelayDomain = outputRelayRepository.findByName(outputRelay.getName());
            brickService.deleteDomain(outputRelayDomain.getBrickDomain());
            outputRelayRepository.delete(outputRelayDomain);
        }

        brickService.disconnectAll();
    }


    @Test
    public void testRelay() {

        LOG.debug("START testRelay");

        for (Relay outputRelay : outputRelays) {
            outputRelay.setStates(false, true);

            assertArrayEquals(new boolean[] { false, true }, outputRelay.getStates());
        }

        LOG.debug("FINISH testRelay");
    }
}
