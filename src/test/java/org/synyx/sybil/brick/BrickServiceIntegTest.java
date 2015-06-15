package org.synyx.sybil.brick;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
import org.synyx.sybil.config.DevSpringConfig;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


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

        test1 = brickService.getBrickDomain("one");

        LEDStripDomain testOne = new LEDStripDomain("testone", "abc", 30, test1);
        testOne = LEDStripRepository.save(testOne);

        assertThat(brickService.getAllBrickDomains().size(), is(oldSize + 3)); // assert that 3 bricks were added

        brickService.disconnectAll();
        brickService.connectAll();

        LEDStrip LEDStrip = LEDStripRegistry.get(testOne);

        Color color = new Color(16, 35, 77);

        LEDStrip.setPixel(1, color);
        LEDStrip.updateDisplay();

        Color pixel0 = LEDStrip.getPixel(0);
        Color pixel1 = LEDStrip.getPixel(1);

        assertThat(pixel0.getRedAsShort(), is((short) 0));
        assertThat(pixel0.getGreenAsShort(), is((short) 0));
        assertThat(pixel0.getBlueAsShort(), is((short) 0));

        assertThat(pixel1.getRedAsShort(), is((short) 16));
        assertThat(pixel1.getGreenAsShort(), is((short) 35));
        assertThat(pixel1.getBlueAsShort(), is((short) 77));

        brickService.deleteBrickDomain(brickService.getBrickDomain("one"));
        brickService.deleteBrickDomain(brickService.getBrickDomain("two"));
        brickService.deleteBrickDomain(brickService.getBrickDomain("three"));

        LEDStripRepository.delete(testOne);

        // disconnect all bricks & bricklets
        brickService.disconnectAll();

        LOG.info("FINISH Test testConnectALL");
    }
}
