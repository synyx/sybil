package org.synyx.sybil.brick;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.NotConnectedException;

import org.junit.After;
import org.junit.Assert;
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
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.out.Color;
import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.OutputLEDStripRegistry;

import java.io.IOException;


@ContextConfiguration(classes = DevSpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class BrickRegistryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(BrickRegistryIntegTest.class);

    @Autowired
    private OutputLEDStripRegistry outputLEDStripRegistry;

    @Autowired
    private OutputLEDStripRepository outputLEDStripRepository;

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
        OutputLEDStripDomain testOne = new OutputLEDStripDomain("testone", "abc", 30, test1);
        OutputLEDStripDomain testTwo = new OutputLEDStripDomain("testtwo", "def", 30, test2);
        OutputLEDStripDomain testThree = new OutputLEDStripDomain("testthree", "ghi", 30, test3);

        // add them to the database
        testOne = outputLEDStripRepository.save(testOne);
        testTwo = outputLEDStripRepository.save(testTwo);
        testThree = outputLEDStripRepository.save(testThree);

        // initialise LED Strips (fetching them from the database on the way)
        outputLEDStripRegistry.get(testOne);
        outputLEDStripRegistry.get(testTwo);
        outputLEDStripRegistry.get(testThree);
    }


    @After
    public void close() throws NotConnectedException {

        OutputLEDStripDomain testOneDomain = outputLEDStripRepository.findByName("testone");
        OutputLEDStripDomain testTwoDomain = outputLEDStripRepository.findByName("testtwo");
        OutputLEDStripDomain testThreeDomain = outputLEDStripRepository.findByName("testthree");

        brickRepository.delete(testOneDomain.getBrickDomain());
        brickRepository.delete(testTwoDomain.getBrickDomain());
        brickRepository.delete(testThreeDomain.getBrickDomain());

        outputLEDStripRepository.delete(testOneDomain);
        outputLEDStripRepository.delete(testTwoDomain);
        outputLEDStripRepository.delete(testThreeDomain);

        // disconnect all bricks & bricklets
        brickRegistry.disconnectAll();
    }


    @Test
    public void testDisconnectAll() throws NotConnectedException, IOException, AlreadyConnectedException {

        LOG.info("START Test testDisconnectALL");

        brickRegistry.disconnectAll();

        Color color = new Color(16, 35, 77);

        OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("testone"));

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);

        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRedAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreenAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlueAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRedAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreenAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlueAsShort());

        LOG.info("FINISH Test testDisconnectALL");
    }


    @Test
    public void testConnectAll() throws NotConnectedException, IOException, AlreadyConnectedException {

        LOG.info("START Test testConnectALL");

        brickRegistry.disconnectAll();
        brickRegistry.connectAll();

        Color color = new Color(16, 35, 77);

        OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("testone"));

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);

        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRedAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreenAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlueAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRedAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreenAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlueAsShort());

        LOG.info("FINISH Test testConnectALL");
    }


    @Test
    public void testReconnectAll() throws NotConnectedException, IOException, AlreadyConnectedException {

        LOG.info("START Test testReconnectALL");

        brickRegistry.reconnectAll();

        Color color = new Color(16, 35, 77);

        OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("testone"));

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);

        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRedAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreenAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlueAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRedAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreenAsShort());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlueAsShort());

        LOG.info("FINISH Test testReconnectALL");
    }
}
