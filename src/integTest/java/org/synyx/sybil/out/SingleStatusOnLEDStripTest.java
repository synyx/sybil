package org.synyx.sybil.out;

import com.tinkerforge.NotConnectedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;

import static org.junit.Assert.assertTrue;


public class SingleStatusOnLEDStripTest {

    private SingleStatusOutput singleStatusOutput;
    private OutputLEDStrip outputLEDStrip;

    @Before
    public void setup() {

        outputLEDStrip = OutputLEDStripRegistry.get("Devkit");
        singleStatusOutput = new SingleStatusOnLEDStrip(outputLEDStrip);
    }


    @After
    public void close() throws NotConnectedException {

        outputLEDStrip.setFill(Color.BLACK); // turn off the LEDs
        outputLEDStrip.updateDisplay();
    }


    @Test
    public void testShowStatusWarning() throws Exception {

        singleStatusOutput.showStatus(new StatusInformation("Integration Test", Status.WARNING));

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("LED Strip should be yellow",
            pixel.getRed() == 127 && pixel.getGreen() == 127 && pixel.getBlue() == 0);
    }


    @Test
    public void testShowStatusCritical() throws Exception {

        singleStatusOutput.showStatus(new StatusInformation("Integration Test", Status.CRITICAL));

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("LED Strip should be red", pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
    }


    @Test
    public void testShowStatusOkay() throws Exception {

        singleStatusOutput.showStatus(new StatusInformation("Integration Test", Status.OKAY));

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("LED Strip should be black", pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
    }
}
