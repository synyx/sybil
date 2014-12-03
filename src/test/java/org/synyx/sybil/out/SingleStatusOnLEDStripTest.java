package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;

import java.io.IOException;

import static org.junit.Assert.assertTrue;


public class SingleStatusOnLEDStripTest {

    private IPConnection ipConnection;
    private OutputLEDStrip outputLEDStrip;
    private SingleStatusOutput singleStatusOutput;

    @Before
    public void setup() throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

        ipConnection = new IPConnection();

        ipConnection.connect("localhost", 4223);

        BrickletLEDStrip ledStrip = new BrickletLEDStrip("p3c", ipConnection);

        ledStrip.setChipType(2812);

        ledStrip.setFrameDuration(10);

        outputLEDStrip = new OutputLEDStrip(ledStrip, 30);

        singleStatusOutput = new SingleStatusOnLEDStrip(outputLEDStrip);
    }


    @After
    public void close() throws NotConnectedException {

        outputLEDStrip.setColor(Color.BLACK); // turn off the LEDs
        outputLEDStrip.updateDisplay();

        if (ipConnection != null) {
            ipConnection.disconnect();
        }
    }


    @Test
    public void testShowStatusWarning() throws Exception {

        singleStatusOutput.showStatus(new StatusInformation("Unittest", Status.WARNING));

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("LED Strip should be yellow",
            pixel.getRed() == 127 && pixel.getGreen() == 127 && pixel.getBlue() == 0);
    }


    @Test
    public void testShowStatusCritical() throws Exception {

        singleStatusOutput.showStatus(new StatusInformation("Unittest", Status.CRITICAL));

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("LED Strip should be red", pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
    }


    @Test
    public void testShowStatusOkay() throws Exception {

        singleStatusOutput.showStatus(new StatusInformation("Unittest.", Status.OKAY));

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("LED Strip should be black", pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
    }
}
