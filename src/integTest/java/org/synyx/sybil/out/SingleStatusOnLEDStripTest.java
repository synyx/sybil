package org.synyx.sybil.out;

import com.tinkerforge.NotConnectedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.config.SpringConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;

import static org.junit.Assert.assertTrue;


@ContextConfiguration(classes = SpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SingleStatusOnLEDStripTest {

    private OutputLEDStrip devkitOne;
    private OutputLEDStrip devkitTwo;
    private SingleStatusOutput singleStatusOutputOne;
    private SingleStatusOutput singleStatusOutputTwo;

    @Autowired
    OutputLEDStripRegistry outputLEDStripRegistry;

    @Autowired
    OutputLEDStripRepository outputLEDStripRepository;

    @Autowired
    private BrickRepository brickRepository;

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

        OutputLEDStripDomain devkitOneDomain = new OutputLEDStripDomain("DevkitOne", "p5V", 30, localUSB);
        OutputLEDStripDomain devkitTwoDomain = new OutputLEDStripDomain("DevkitTwo", "p3c", 30, synerforge001);

        outputLEDStripRepository.save(devkitOneDomain);
        outputLEDStripRepository.save(devkitTwoDomain);

        devkitOne = outputLEDStripRegistry.get(devkitOneDomain);
        devkitTwo = outputLEDStripRegistry.get(devkitTwoDomain);

        singleStatusOutputOne = new SingleStatusOnLEDStrip(devkitOne);
        singleStatusOutputTwo = new SingleStatusOnLEDStrip(devkitTwo);
    }


    @After
    public void close() throws NotConnectedException {

        devkitOne.setFill(Color.BLACK); // turn off the LEDs
        devkitOne.updateDisplay();
        devkitTwo.setFill(Color.BLACK); // turn off the LEDs
        devkitTwo.updateDisplay();
    }


    @Test
    public void testShowStatusWarning() throws Exception {

        StatusInformation statusInformation = new StatusInformation("Integration Test", Status.WARNING);

        singleStatusOutputOne.showStatus(statusInformation);

        Color pixel = devkitOne.getPixel(0);
        assertTrue("LED Strip should be yellow",
            pixel.getRed() == 127 && pixel.getGreen() == 127 && pixel.getBlue() == 0);
        // Thread.sleep(2000);
    }


    @Test
    public void testShowStatusCritical() throws Exception {

        StatusInformation statusInformation = new StatusInformation("Integration Test", Status.CRITICAL);

        singleStatusOutputTwo.showStatus(statusInformation);

        Color pixel = devkitTwo.getPixel(0);
        assertTrue("LED Strip should be red", pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        // Thread.sleep(2000);
    }


    @Test
    public void testShowStatusOkay() throws Exception {

        StatusInformation statusInformation = new StatusInformation("Integration Test", Status.OKAY);

        singleStatusOutputOne.showStatus(statusInformation);

        Color pixel = devkitOne.getPixel(0);
        assertTrue("LED Strip should be black", pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
    }
}
