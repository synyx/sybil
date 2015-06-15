package org.synyx.sybil.brick;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
import org.synyx.sybil.config.DevSpringConfig;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.fail;


@ContextConfiguration(classes = DevSpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class BrickServiceIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(BrickServiceIntegTest.class);

    @Autowired
    private LEDStripRegistry LEDStripRegistry;

    @Autowired
    private LEDStripRepository LEDStripRepository;

    @Autowired
    private BrickService brickService;

    @Test
    public void testReConnectAll() throws Exception {

        LOG.info("START Test testConnectALL");

        List<BrickDomain> bricks = new ArrayList<>();
        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223, "one");
        BrickDomain test2 = new BrickDomain("localhost", "im666", 14224, "two");

        bricks.add(test1);
        bricks.add(test2);

        BrickDomain test3 = new BrickDomain("localhost", "123abc", 14225, "three");

        int oldSize = brickService.getAllBrickDomains().size();

        brickService.saveBrickDomains(bricks);
        brickService.saveBrickDomain(test3);

        assertThat(brickService.getAllBrickDomains().size(), is(oldSize + 3)); // assert that 3 bricks were added

        IPConnection ipConnection = brickService.getIPConnection(test3);

        brickService.disconnectAll();

        try {
            BrickMaster brickMaster = brickService.getBrickMaster("123abc", ipConnection);
            brickMaster.getChipTemperature();
            fail("NotConnectedException should have been thrown.");
        } catch (NotConnectedException e) {
            // This is what we want!
        }

        brickService.connectAll();

        ipConnection = brickService.getIPConnection(test3);

        BrickMaster brickMaster = brickService.getBrickMaster("123abc", ipConnection);
        brickMaster.getChipTemperature();

        brickService.deleteBrickDomain(brickService.getBrickDomain("one"));
        brickService.deleteBrickDomain(brickService.getBrickDomain("two"));
        brickService.deleteBrickDomain(brickService.getBrickDomain("three"));

        brickService.disconnectAll();

        LOG.info("FINISH Test testConnectALL");
    }
}
