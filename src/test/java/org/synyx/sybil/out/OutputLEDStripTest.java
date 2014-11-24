package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;


public class OutputLEDStripTest {

    private IPConnection ipConnection;
    private OutputLEDStrip outputLEDStrip;

    @Before
    public void setup() throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

        ipConnection = new IPConnection();

        ipConnection.connect("localhost", 4223);

        BrickletLEDStrip ledStrip = new BrickletLEDStrip("p3c", ipConnection);

        ledStrip.setChipType(2812);

        ledStrip.setFrameDuration(10);

        outputLEDStrip = new OutputLEDStrip(ledStrip, 30);
    }


    @After
    public void close() throws NotConnectedException {

        outputLEDStrip.setColor(Color.BLACK);

        if (ipConnection != null) {
            ipConnection.disconnect();
        }
    }


    @Test
    public void testSetColor() throws Exception {

        outputLEDStrip.setColor(new Color(16, 32, 8));

        assertTrue("Pixel 0 should be 16, 32, 8.",
            outputLEDStrip.getPixel(0).getRed() == 16 && outputLEDStrip.getPixel(0).getGreen() == 32
            && outputLEDStrip.getPixel(0).getBlue() == 8);
    }


    @Test
    public void testSetPixel() throws Exception {

        Color color = new Color(16, 35, 77);

        outputLEDStrip.setPixel(1, color);

        assertTrue("Pixel 1 should be 16, 35, 77; Pixel 0 should be 0, 0, 0",
            outputLEDStrip.getPixel(1).getRed() == 16 && outputLEDStrip.getPixel(1).getGreen() == 35
            && outputLEDStrip.getPixel(1).getBlue() == 77 && outputLEDStrip.getPixel(0).getRed() == 0
            && outputLEDStrip.getPixel(0).getGreen() == 0 && outputLEDStrip.getPixel(0).getBlue() == 0);
    }


    @Test
    public void testSetBrightnessHalf() throws Exception {

        outputLEDStrip.setColor(Color.WHITE);

        outputLEDStrip.setBrightness(.5);

        assertTrue("Pixel 0 should be half as bright as a full white (127, 127, 127).",
            outputLEDStrip.getPixel(0).getRed() == (short) (127 * .5)
            && outputLEDStrip.getPixel(0).getGreen() == (short) (127 * .5)
            && outputLEDStrip.getPixel(0).getBlue() == (short) (127 * .5));
    }


    @Test
    public void testSetBrightnessFull() throws Exception {

        outputLEDStrip.setColor(Color.WHITE);

        outputLEDStrip.setBrightness(1);

        assertTrue("Pixel 0 should be full white (127, 127, 127).",
            outputLEDStrip.getPixel(0).getRed() == 127 && outputLEDStrip.getPixel(0).getGreen() == 127
            && outputLEDStrip.getPixel(0).getBlue() == 127);
    }


    @Test
    public void testSetBrightnessDouble() throws Exception {

        outputLEDStrip.setColor(Color.WHITE);

        outputLEDStrip.setBrightness(2);

        assertTrue("Pixel 0 should be double as bright as a full white (127, 127, 127).",
            outputLEDStrip.getPixel(0).getRed() == (short) (127 * 2)
            && outputLEDStrip.getPixel(0).getGreen() == (short) (127 * 2)
            && outputLEDStrip.getPixel(0).getBlue() == (short) (127 * 2));
    }
}
