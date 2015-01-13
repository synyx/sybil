package org.synyx.sybil.out;

import org.junit.After;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.config.SpringConfigDev;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringConfigDev.class })
public class OutputLEDStripTest {

    private static final Logger LOG = LoggerFactory.getLogger(OutputLEDStripTest.class);

    @Autowired
    private OutputLEDStrip outputLEDStrip;

    @Autowired
    Environment env;

    @After
    public void close() { // throws NotConnectedException {

        outputLEDStrip.setBrightness(1.0);
        outputLEDStrip.setFill(Color.BLACK);
        outputLEDStrip.updateDisplay();
    }


    @Test
    public void testSetColor() throws Exception {

        LOG.info("START Test testSetColor");

        outputLEDStrip.setFill(new Color(16, 32, 8));
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertEquals("Pixel 0.red should be 16", 16, pixel.getRed());
        assertEquals("Pixel 0.green should be 32", 32, pixel.getGreen());
        assertEquals("Pixel 0.blue should be 8", 8, pixel.getBlue());

        LOG.info("FINISHED Test testSetColor");
    }


    @Test
    public void testSetPixel() throws Exception {

        LOG.info("START Test testSetPixel");

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

        LOG.info("FINISHED Test testSetPixel");
    }


    @Test
    public void testSetBrightnessHalf() throws Exception {

        LOG.info("START Test testBrightnessHalf");

        outputLEDStrip.setFill(Color.WHITE);

        outputLEDStrip.setBrightness(.5);
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("Pixel 0 should be half as bright as a full white (127, 127, 127).",
            pixel.getRed() == (short) (127 * .5) && pixel.getGreen() == (short) (127 * .5)
            && pixel.getBlue() == (short) (127 * .5));

        LOG.info("FINISH Test testBrightnessHalf");
    }


    @Test
    public void testSetBrightnessFull() throws Exception {

        LOG.info("START Test testBrightnessFull");

        outputLEDStrip.setFill(Color.WHITE);

        outputLEDStrip.setBrightness(1);
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("Pixel 0 should be full white (127, 127, 127).",
            pixel.getRed() == 127 && pixel.getGreen() == 127 && pixel.getBlue() == 127);

        LOG.info("FINISH Test testBrightnessFull");
    }


    @Test
    public void testSetBrightnessDouble() throws Exception {

        LOG.info("START Test testBrightnessDouble");

        outputLEDStrip.setFill(Color.WHITE);

        outputLEDStrip.setBrightness(2);
        outputLEDStrip.updateDisplay();

        Color pixel = outputLEDStrip.getPixel(0);
        assertTrue("Pixel 0 should be double as bright as a full white (127, 127, 127).",
            pixel.getRed() == (short) (127 * 2) && pixel.getGreen() == (short) (127 * 2)
            && pixel.getBlue() == (short) (127 * 2));

        LOG.info("FINISH Test testBrightnessDouble");
    }


    @Test
    public void testDrawSprite() {

        LOG.info("START Test testDrawSprite");

        Sprite1D sprite = new Sprite1D(10);
        sprite.setFill(new Color(127, 0, 0));

        outputLEDStrip.drawSprite(sprite, 5);
        outputLEDStrip.updateDisplay();

        for (int i = 0; i < 5; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be black",
                pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        for (int i = 5; i < 15; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be red",
                pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        for (int i = 15; i < 30; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be black",
                pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        LOG.info("FINISH Test testDrawSprite");
    }


    @Test
    public void testDrawSpriteWithoutWrap() {

        LOG.info("START Test testDrawSpriteWithoutWrap");

        Sprite1D sprite = new Sprite1D(10);
        sprite.setFill(new Color(127, 0, 0));

        outputLEDStrip.drawSprite(sprite, 25);
        outputLEDStrip.updateDisplay();

        for (int i = 0; i < 25; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be black",
                pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        for (int i = 25; i < 30; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be red",
                pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        LOG.info("FINISH Test testDrawSpriteWithoutWrap");
    }


    @Test
    public void testDrawSpriteWithWrap() {

        LOG.info("START Test testDrawSpriteWithWrap");

        Sprite1D sprite = new Sprite1D(10);
        sprite.setFill(new Color(127, 0, 0));

        outputLEDStrip.drawSprite(sprite, 25, true);
        outputLEDStrip.updateDisplay();

        for (int i = 0; i < 5; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be red",
                pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        for (int i = 5; i < 25; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be black",
                pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        for (int i = 25; i < 30; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be red",
                pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        LOG.info("FINISH Test testDrawSpriteWithWrap");
    }


    @Test
    public void testDrawSpriteOverlong() {

        LOG.info("START Test testDrawSpriteOverlong");

        Sprite1D sprite = new Sprite1D(50);
        sprite.setFill(new Color(127, 0, 0));

        outputLEDStrip.drawSprite(sprite, 25, true);
        outputLEDStrip.updateDisplay();

        for (int i = 0; i < 30; i++) {
            Color pixel = outputLEDStrip.getPixel(i);
            assertTrue("Pixel " + i + " should be red",
                pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
        }

        LOG.info("FINISH Test testDrawSpriteOverlong");
    }


    @Test
    public void testGetLength() {

        int ledStripLength = outputLEDStrip.getLength();
        int expectedLength = env.getRequiredProperty("outputledstrip.length", Integer.class);
        assertEquals(expectedLength, ledStripLength);
    }
}
