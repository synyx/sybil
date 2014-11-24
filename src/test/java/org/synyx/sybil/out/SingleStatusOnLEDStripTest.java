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
    private SingleStatusOutput out;

    @Before
    public void setup() throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

        ipConnection = new IPConnection();

        ipConnection.connect("localhost", 4223);

        BrickletLEDStrip ledStrip = new BrickletLEDStrip("p3c", ipConnection);

        ledStrip.setChipType(2812);

        ledStrip.setFrameDuration(10);

        outputLEDStrip = new OutputLEDStrip(ledStrip, 30);

        out = new SingleStatusOnLEDStrip(outputLEDStrip);
    }


    @After
    public void close() throws NotConnectedException {

        outputLEDStrip.setColor(Color.BLACK); // turn off the LEDs

        if (ipConnection != null) {
            ipConnection.disconnect();
        }
    }


    @Test
    public void testShowStatusWarning() throws Exception {

        out.showStatus(new StatusInformation("Yellow Alert", Status.WARNING));
        assertTrue("LED Strip should be yellow",
            outputLEDStrip.getPixel(0).getRed() == 127 && outputLEDStrip.getPixel(0).getGreen() == 127
            && outputLEDStrip.getPixel(0).getBlue() == 0);
    }


    @Test
    public void testShowStatusCritical() throws Exception {

        out.showStatus(new StatusInformation("Red Alert", Status.CRITICAL));
        assertTrue("LED Strip should be red",
            outputLEDStrip.getPixel(0).getRed() == 127 && outputLEDStrip.getPixel(0).getGreen() == 0
            && outputLEDStrip.getPixel(0).getBlue() == 0);
    }


    @Test
    public void testShowStatusOkay() throws Exception {

        out.showStatus(new StatusInformation("Everything's shiny, Cap'n, not to fret.", Status.OKAY));
        assertTrue("LED Strip should be black",
            outputLEDStrip.getPixel(0).getRed() == 0 && outputLEDStrip.getPixel(0).getGreen() == 0
            && outputLEDStrip.getPixel(0).getBlue() == 0);
    }
}
