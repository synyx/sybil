package org.synyx.sybil.out;

import org.junit.After;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/applicationContext.xml")
public class OutputLEDStripTest {

    @Autowired
    private OutputLEDStrip outputLEDStrip;

    @After
    public void close() { // throws NotConnectedException {

        outputLEDStrip.setBrightness(1.0);
        outputLEDStrip.setColor(Color.BLACK);
        outputLEDStrip.updateDisplay();
    }


    @Test
    public void testSetColor() throws Exception {

        outputLEDStrip.setColor(new Color(16, 32, 8));
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertEquals("Pixel 0.red should be 16", 16, pixel.getRed());
        assertEquals("Pixel 0.green should be 32", 32, pixel.getGreen());
        assertEquals("Pixel 0.blue should be 8", 8, pixel.getBlue());
    }


    @Test
    public void testSetPixel() throws Exception {

        Color color = new Color(16, 35, 77);

        outputLEDStrip.setPixel(1, color);
        outputLEDStrip.updateDisplay();

        Color pixel0 = outputLEDStrip.getPixel(0);
        Color pixel1 = outputLEDStrip.getPixel(1);

        assertEquals("Pixel 0.red should be 0", 0, pixel0.getRed());
        assertEquals("Pixel 0.green should be 0", 0, pixel0.getGreen());
        assertEquals("Pixel 0.blue should be 0", 0, pixel0.getBlue());
        assertEquals("Pixel 1.red should be 16", 16, pixel1.getRed());
        assertEquals("Pixel 1.green should be 35", 35, pixel1.getGreen());
        assertEquals("Pixel 1.blue should be 77", 77, pixel1.getBlue());
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
