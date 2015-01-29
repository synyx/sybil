package org.synyx.sybil.common;

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

import org.synyx.sybil.config.SpringConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.out.Color;
import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.OutputLEDStripRegistry;

import java.io.IOException;


@ContextConfiguration(classes = SpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class BrickRegistryTest {

    private static final Logger LOG = LoggerFactory.getLogger(BrickRegistryTest.class);

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

        // clear the test database
        outputLEDStripRepository.deleteAll();
        brickRepository.deleteAll();

        // define Bricks
        BrickDomain localUSB = new BrickDomain("localhost");
        BrickDomain synerforge001 = new BrickDomain("synerforge001");

        // add them to the database
        brickRepository.save(localUSB);
        brickRepository.save(synerforge001);

        // define LED Strips (bricklets)
        OutputLEDStripDomain devkitOne = new OutputLEDStripDomain("DevkitOne", "p5V", 30, localUSB);
        OutputLEDStripDomain devkitTwo = new OutputLEDStripDomain("DevkitTwo", "p3c", 30, synerforge001);
        OutputLEDStripDomain devkitDummy = new OutputLEDStripDomain("DevkitDummy", "p3B", 30, synerforge001);

        // add them to the database
        devkitOne = outputLEDStripRepository.save(devkitOne);
        devkitTwo = outputLEDStripRepository.save(devkitTwo);
        devkitDummy = outputLEDStripRepository.save(devkitDummy);

        // initialise LED Strips (fetching them from the database on the way), cast and add them to the list
        outputLEDStripRegistry.get(devkitOne);
        outputLEDStripRegistry.get(devkitTwo);
        outputLEDStripRegistry.get(devkitDummy);
    }


    @After
    public void close() throws NotConnectedException {

        // disconnect all bricks & bricklets
        brickRegistry.disconnectAll();
    }


    @Test
    public void testDisconnectAll() throws NotConnectedException, IOException, AlreadyConnectedException {

        LOG.info("START Test testDisconnectALL");

        brickRegistry.disconnectAll();

        Color color = new Color(16, 35, 77);

        OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("DevkitOne"));

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);

        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRed());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreen());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlue());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRed());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreen());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlue());

        LOG.info("FINISH Test testDisconnectALL");
    }


    @Test
    public void testConnectAll() throws NotConnectedException, IOException, AlreadyConnectedException {

        LOG.info("START Test testConnectALL");

        brickRegistry.disconnectAll();
        brickRegistry.connectAll();

        Color color = new Color(16, 35, 77);

        OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("DevkitOne"));

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);

        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRed());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreen());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlue());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRed());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreen());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlue());

        LOG.info("FINISH Test testConnectALL");
    }


    @Test
    public void testReconnectAll() throws NotConnectedException, IOException, AlreadyConnectedException {

        LOG.info("START Test testReconnectALL");

        brickRegistry.reconnectAll();

        Color color = new Color(16, 35, 77);

        OutputLEDStrip outputLEDStrip = outputLEDStripRegistry.get(outputLEDStripRepository.findByName("DevkitOne"));

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);

        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRed());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreen());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlue());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRed());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreen());
        Assert.assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlue());

        LOG.info("FINISH Test testReconnectALL");
    }
}
