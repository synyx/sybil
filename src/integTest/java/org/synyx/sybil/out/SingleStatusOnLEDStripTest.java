package org.synyx.sybil.out;

import com.tinkerforge.NotConnectedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.common.BrickletRegistry;
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
    BrickletRegistry brickletRegistry;

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

        OutputLEDStripDomain devkitOne = new OutputLEDStripDomain("DevkitOne", "p5V", 30, localUSB);
        OutputLEDStripDomain devkitTwo = new OutputLEDStripDomain("DevkitTwo", "p3c", 30, synerforge001);

        outputLEDStripRepository.save(devkitOne);
        outputLEDStripRepository.save(devkitTwo);

        this.devkitOne = (OutputLEDStrip) brickletRegistry.get(devkitOne);
        this.devkitTwo = (OutputLEDStrip) brickletRegistry.get(devkitTwo);

        singleStatusOutputOne = new SingleStatusOnLEDStrip(this.devkitOne);
        singleStatusOutputTwo = new SingleStatusOnLEDStrip(this.devkitTwo);
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

        singleStatusOutputOne.showStatus(new StatusInformation("Integration Test", Status.WARNING));

        Color pixel = devkitOne.getPixel(0);
        assertTrue("LED Strip should be yellow",
            pixel.getRed() == 127 && pixel.getGreen() == 127 && pixel.getBlue() == 0);
        // Thread.sleep(2000);
    }


    @Test
    public void testShowStatusCritical() throws Exception {

        singleStatusOutputTwo.showStatus(new StatusInformation("Integration Test", Status.CRITICAL));

        Color pixel = devkitTwo.getPixel(0);
        assertTrue("LED Strip should be red", pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        // Thread.sleep(2000);
    }


    @Test
    public void testShowStatusOkay() throws Exception {

        singleStatusOutputOne.showStatus(new StatusInformation("Integration Test", Status.OKAY));

        Color pixel = devkitOne.getPixel(0);
        assertTrue("LED Strip should be black", pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
    }
}
