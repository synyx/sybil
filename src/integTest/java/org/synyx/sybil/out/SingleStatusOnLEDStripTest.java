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

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;


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
        BrickDomain devkit1 = new BrickDomain("localhost", 14223);
        BrickDomain devkit2 = new BrickDomain("localhost", 14224);

        // add them to the database
        brickRepository.save(devkit1);
        brickRepository.save(devkit2);

        // define LED Strips (bricklets)
        OutputLEDStripDomain devkitOneDomain = new OutputLEDStripDomain("devkitone", "abc", 30, devkit1);
        OutputLEDStripDomain devkitTwoDomain = new OutputLEDStripDomain("devkittwo", "def", 30, devkit2);

        // add them to the database
        devkitOneDomain = outputLEDStripRepository.save(devkitOneDomain);
        devkitTwoDomain = outputLEDStripRepository.save(devkitTwoDomain);

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
        assertThat(pixel.getRedAsShort(), is((short) 127));
        assertThat(pixel.getGreenAsShort(), is((short) 127));
        assertThat(pixel.getBlueAsShort(), is((short) 0));
    }


    @Test
    public void testShowStatusCritical() throws Exception {

        StatusInformation statusInformation = new StatusInformation("Integration Test", Status.CRITICAL);

        singleStatusOutputTwo.showStatus(statusInformation);

        Color pixel = devkitTwo.getPixel(0);
        assertThat(pixel.getRedAsShort(), is((short) 127));
        assertThat(pixel.getGreenAsShort(), is((short) 0));
        assertThat(pixel.getBlueAsShort(), is((short) 0));
    }


    @Test
    public void testShowStatusOkay() throws Exception {

        StatusInformation statusInformation = new StatusInformation("Integration Test", Status.OKAY);

        singleStatusOutputOne.showStatus(statusInformation);

        Color pixel = devkitOne.getPixel(0);
        assertThat(pixel.getRedAsShort(), is((short) 0));
        assertThat(pixel.getGreenAsShort(), is((short) 16));
        assertThat(pixel.getBlueAsShort(), is((short) 0));
    }
}
