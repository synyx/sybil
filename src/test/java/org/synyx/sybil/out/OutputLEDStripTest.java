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
        outputLEDStrip.updateDisplay();

        if (ipConnection != null) {
            ipConnection.disconnect();
        }
    }


    @Test
    public void testSetColor() throws Exception {

        outputLEDStrip.setColor(new Color(16, 32, 8));
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("Pixel 0 should be 16, 32, 8.",
            pixel.getRed() == 16 && pixel.getGreen() == 32 && pixel.getBlue() == 8);
    }


    @Test
    public void testSetPixel() throws Exception {

        Color color = new Color(16, 35, 77);

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);
        assertTrue("Pixel 1 should be 16, 35, 77; Pixel 0 should be 0, 0, 0",
            pixel1.getRed() == 16 && pixel1.getGreen() == 35 && pixel1.getBlue() == 77 && pixel0.getRed() == 0
            && pixel0.getGreen() == 0 && pixel0.getBlue() == 0);
    }


    @Test
    public void testSetBrightnessHalf() throws Exception {

        outputLEDStrip.setColor(Color.WHITE);

        outputLEDStrip.setBrightness(.5);
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("Pixel 0 should be half as bright as a full white (127, 127, 127).",
            pixel.getRed() == (short) (127 * .5) && pixel.getGreen() == (short) (127 * .5)
            && pixel.getBlue() == (short) (127 * .5));
    }


    @Test
    public void testSetBrightnessFull() throws Exception {

        outputLEDStrip.setColor(Color.WHITE);

        outputLEDStrip.setBrightness(1);
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("Pixel 0 should be full white (127, 127, 127).",
            pixel.getRed() == 127 && pixel.getGreen() == 127 && pixel.getBlue() == 127);
    }


    @Test
    public void testSetBrightnessDouble() throws Exception {

        outputLEDStrip.setColor(Color.WHITE);

        outputLEDStrip.setBrightness(2);
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("Pixel 0 should be double as bright as a full white (127, 127, 127).",
            pixel.getRed() == (short) (127 * 2) && pixel.getGreen() == (short) (127 * 2)
            && pixel.getBlue() == (short) (127 * 2));
    }
}
