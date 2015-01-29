package org.synyx.sybil.out;

import com.tinkerforge.NotConnectedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.common.BrickRegistry;
import org.synyx.sybil.config.SpringConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@ContextConfiguration(classes = SpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OutputLEDStripTest {

    private static final Logger LOG = LoggerFactory.getLogger(OutputLEDStripTest.class);

    private List<OutputLEDStrip> outputLEDStrips = new ArrayList<>();

    @Autowired
    private OutputLEDStripRegistry outputLEDStripRegistry;

    @Autowired
    private OutputLEDStripRepository outputLEDStripRepository;

    @Autowired
    private BrickRepository brickRepository;

    @Autowired
    private BrickRegistry brickRegistry;

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

        // define LED Strips (bricklets)
        OutputLEDStripDomain devkitOne = new OutputLEDStripDomain("devkitone", "p5V", 30, localUSB);
        OutputLEDStripDomain devkitTwo = new OutputLEDStripDomain("devkittwo", "p3c", 30, synerforge001);
        OutputLEDStripDomain devkitDummy = new OutputLEDStripDomain("devkitdummy", "p3B", 30, synerforge001);

        // add them to the database
        devkitOne = outputLEDStripRepository.save(devkitOne);
        devkitTwo = outputLEDStripRepository.save(devkitTwo);
        devkitDummy = outputLEDStripRepository.save(devkitDummy);

        // initialise LED Strips (fetching them from the database on the way), cast and add them to the list
        outputLEDStrips.add(outputLEDStripRegistry.get(devkitOne));
        outputLEDStrips.add(outputLEDStripRegistry.get(devkitTwo));
        outputLEDStrips.add(outputLEDStripRegistry.get(devkitDummy));
    }


    @After
    public void close() throws NotConnectedException {

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) { // iterate over list of strips
            outputLEDStrip.setBrightness(1.0); // set brightness to normal
            outputLEDStrip.setFill(Color.BLACK); // set color to black (i.e. turn all LEDs off)
            outputLEDStrip.updateDisplay(); // make it so!
        }

        // disconnect all bricks & bricklets
//        try {
        brickRegistry.disconnectAll();
//        } catch (NotConnectedException e) {
//            LOG.error(e.toString());
//        }
    }


    @Test
    public void testSetColor() throws Exception {

        LOG.info("START Test testSetColor");

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.setFill(new Color(16, 32, 8));
            outputLEDStrip.updateDisplay();

            Color pixel = outputLEDStrip.getPixel(0);
            assertEquals(outputLEDStrip.getName() + " Pixel  0.red should be 16", 16, pixel.getRed());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 32", 32, pixel.getGreen());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 8", 8, pixel.getBlue());
        }

        LOG.info("FINISHED Test testSetColor");
    }


    @Test
    public void testSetPixel() throws Exception {

        LOG.info("START Test testSetPixel");

        Color color = new Color(16, 35, 77);

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.setPixel(1, color);
            outputLEDStrip.updateDisplay();

            Color pixel0 = outputLEDStrip.getPixel(0);
            Color pixel1 = outputLEDStrip.getPixel(1);

            assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRed());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreen());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlue());
            assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRed());
            assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreen());
            assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlue());
        }

        LOG.info("FINISHED Test testSetPixel");
    }


    @Test
    public void testSetBrightnessHalf() throws Exception {

        LOG.info("START Test testBrightnessHalf");

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.setFill(Color.WHITE);

            outputLEDStrip.setBrightness(.5);
            outputLEDStrip.updateDisplay();

            Color pixel = outputLEDStrip.getPixel(0);
            assertTrue(outputLEDStrip.getName() + " Pixel 0 should be half as bright as a full white (127, 127, 127).",
                pixel.getRed() == (short) (127 * .5) && pixel.getGreen() == (short) (127 * .5)
                && pixel.getBlue() == (short) (127 * .5));
        }

        LOG.info("FINISH Test testBrightnessHalf");
    }


    @Test
    public void testSetBrightnessFull() throws Exception {

        LOG.info("START Test testBrightnessFull");

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.setFill(Color.WHITE);

            outputLEDStrip.setBrightness(1);
            outputLEDStrip.updateDisplay();

            Color pixel = outputLEDStrip.getPixel(0);
            assertTrue(outputLEDStrip.getName() + " Pixel 0 should be full white (127, 127, 127).",
                pixel.getRed() == 127 && pixel.getGreen() == 127 && pixel.getBlue() == 127);
        }

        LOG.info("FINISH Test testBrightnessFull");
    }


    @Test
    public void testSetBrightnessDouble() throws Exception {

        LOG.info("START Test testBrightnessDouble");

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.setFill(Color.WHITE);

            outputLEDStrip.setBrightness(2);
            outputLEDStrip.updateDisplay();

            Color pixel = outputLEDStrip.getPixel(0);
            assertTrue(outputLEDStrip.getName()
                + " Pixel 0 should be double as bright as a full white (127, 127, 127).",
                pixel.getRed() == (short) (127 * 2) && pixel.getGreen() == (short) (127 * 2)
                && pixel.getBlue() == (short) (127 * 2));
        }

        LOG.info("FINISH Test testBrightnessDouble");
    }


    @Test
    public void testDrawSprite() {

        LOG.info("START Test testDrawSprite");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.drawSprite(sprite, 5);
            outputLEDStrip.updateDisplay();

            for (int i = 0; i < 5; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be black",
                    pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }

            for (int i = 5; i < 15; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }

            for (int i = 15; i < 30; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue("Pixel " + i + " should be black",
                    pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }
        }

        LOG.info("FINISH Test testDrawSprite");
    }


    @Test
    public void testDrawSpriteWithoutWrap() {

        LOG.info("START Test testDrawSpriteWithoutWrap");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.drawSprite(sprite, 25);
            outputLEDStrip.updateDisplay();

            for (int i = 0; i < 25; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be black",
                    pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }

            for (int i = 25; i < 30; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }
        }

        LOG.info("FINISH Test testDrawSpriteWithoutWrap");
    }


    @Test
    public void testDrawSpriteWithWrap() {

        LOG.info("START Test testDrawSpriteWithWrap");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.drawSprite(sprite, 25, true);
            outputLEDStrip.updateDisplay();

            for (int i = 0; i < 5; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }

            for (int i = 5; i < 25; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be black",
                    pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }

            for (int i = 25; i < 30; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }
        }

        LOG.info("FINISH Test testDrawSpriteWithWrap");
    }


    @Test
    public void testDrawSpriteOverlong() {

        LOG.info("START Test testDrawSpriteOverlong");

        Sprite1D sprite = new Sprite1D(50, "50 red");
        sprite.setFill(new Color(127, 0, 0));

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.drawSprite(sprite, 25, true);
            outputLEDStrip.updateDisplay();

            for (int i = 0; i < 30; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRed() == 127 && pixel.getGreen() == 0 && pixel.getBlue() == 0);
            }
        }

        LOG.info("FINISH Test testDrawSpriteOverlong");
    }


    @Test
    public void testGetLength() {

        LOG.info("START Test testGetLength");

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            assertEquals(outputLEDStripRepository.findByName(outputLEDStrip.getName()).getLength(),
                outputLEDStrip.getLength());
        }

        LOG.info("FINISH Test testGetLength");
    }
}
