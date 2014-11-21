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


public class SingleStatusOnLEDStripTest {

    private IPConnection ipConnection;
    private BrickletLEDStrip ledStrip;
    private OutputLEDStrip outputLEDStrip;

    @Before
    public void setup() throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

        ipConnection = new IPConnection();

        ipConnection.connect("localhost", 4223);

        ledStrip = new BrickletLEDStrip("p3c", ipConnection);

        ledStrip.setChipType(2812);

        ledStrip.setFrameDuration(10);

        outputLEDStrip = new OutputLEDStrip(ledStrip, 30);
    }


    @After
    public void close() throws NotConnectedException {

        if (ipConnection != null) {
            ipConnection.disconnect();
        }
    }


    @Test
    public void testShowStatus() throws Exception {

        SingleStatusOutput out = new SingleStatusOnLEDStrip(outputLEDStrip);
        System.out.println("Setting to WARNING");
        out.showStatus(new StatusInformation("Test 1", Status.WARNING));
        System.out.println("Sleeping now");
        Thread.sleep(5000);
        System.out.println("Setting to CRITICAL");
        out.showStatus(new StatusInformation("Test 1", Status.CRITICAL));
        System.out.println("Sleeping now");
        Thread.sleep(5000);
        System.out.println("Setting to OKAY");
        out.showStatus(new StatusInformation("Test 1", Status.OKAY));
    }
}
