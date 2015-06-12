package org.synyx.sybil.brick;

import com.tinkerforge.AlreadyConnectedException;
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

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.bricklet.output.ledstrip.Color;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
import org.synyx.sybil.config.DevSpringConfig;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


@ContextConfiguration(classes = DevSpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class BrickRegistryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(BrickRegistryIntegTest.class);

    @Autowired
    private LEDStripRegistry LEDStripRegistry;

    @Autowired
    private LEDStripRepository LEDStripRepository;

    @Autowired
    private BrickRepository brickRepository;

    @Autowired
    private BrickRegistry brickRegistry;

    @Before
    public void setup() {

        // define Bricks
        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);
        BrickDomain test2 = new BrickDomain("localhost", "im666", 14224);
        BrickDomain test3 = new BrickDomain("localhost", "123abc", 14225);

        // add them to the database
        brickRepository.save(test1);
        brickRepository.save(test2);
        brickRepository.save(test3);

        // define LED Strips (bricklets)
        LEDStripDomain testOne = new LEDStripDomain("testone", "abc", 30, test1);
        LEDStripDomain testTwo = new LEDStripDomain("testtwo", "def", 30, test2);
        LEDStripDomain testThree = new LEDStripDomain("testthree", "ghi", 30, test3);

        // add them to the database
        testOne = LEDStripRepository.save(testOne);
        testTwo = LEDStripRepository.save(testTwo);
        testThree = LEDStripRepository.save(testThree);

        // initialise LED Strips (fetching them from the database on the way)
        LEDStripRegistry.get(testOne);
        LEDStripRegistry.get(testTwo);
        LEDStripRegistry.get(testThree);
    }


    @After
    public void close() throws NotConnectedException {

        LEDStripDomain testOneDomain = LEDStripRepository.findByName("testone");
        LEDStripDomain testTwoDomain = LEDStripRepository.findByName("testtwo");
        LEDStripDomain testThreeDomain = LEDStripRepository.findByName("testthree");

        brickRepository.delete(testOneDomain.getBrickDomain());
        brickRepository.delete(testTwoDomain.getBrickDomain());
        brickRepository.delete(testThreeDomain.getBrickDomain());

        LEDStripRepository.delete(testOneDomain);
        LEDStripRepository.delete(testTwoDomain);
        LEDStripRepository.delete(testThreeDomain);

        // disconnect all bricks & bricklets
        brickRegistry.disconnectAll();
    }


    @Test
    public void testReConnectAll() throws NotConnectedException, IOException, AlreadyConnectedException {

        LOG.info("START Test testConnectALL");

        brickRegistry.disconnectAll();
        brickRegistry.connectAll();

        Color color = new Color(16, 35, 77);

        LEDStrip LEDStrip = LEDStripRegistry.get(LEDStripRepository.findByName("testone"));

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

        LOG.info("FINISH Test testConnectALL");
    }
}
