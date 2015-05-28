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
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.database.BrickRepository;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.BrickDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class OutputLEDStripIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(OutputLEDStripIntegTest.class);

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

        // define Bricks
        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);
        BrickDomain test2 = new BrickDomain("localhost", "im666", 14224);
        BrickDomain test3 = new BrickDomain("localhost", "123abc", 14225);

        // add them to the database
        brickRepository.save(test1);
        brickRepository.save(test2);
        brickRepository.save(test3);

        // define LED Strips (bricklets)
        OutputLEDStripDomain testOne = new OutputLEDStripDomain("testone", "abc", 30, test1);
        OutputLEDStripDomain testTwo = new OutputLEDStripDomain("testtwo", "def", 30, test2);
        OutputLEDStripDomain testThree = new OutputLEDStripDomain("testthree", "ghi", 30, test3);

        // add them to the database
        testOne = outputLEDStripRepository.save(testOne);
        testTwo = outputLEDStripRepository.save(testTwo);
        testThree = outputLEDStripRepository.save(testThree);

        // initialise LED Strips (fetching them from the database on the way), cast and add them to the list
        outputLEDStrips.add(outputLEDStripRegistry.get(testOne));
        outputLEDStrips.add(outputLEDStripRegistry.get(testTwo));
        outputLEDStrips.add(outputLEDStripRegistry.get(testThree));
    }


    @After
    public void close() throws NotConnectedException {

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) { // iterate over list of strips

            outputLEDStrip.setBrightness(1.0); // set brightness to normal
            outputLEDStrip.setFill(Color.BLACK); // set color to black (i.e. turn all LEDs off)
            outputLEDStrip.updateDisplay(); // make it so!

            OutputLEDStripDomain ledStripDomain = outputLEDStripRepository.findByName(outputLEDStrip.getName());
            brickRepository.delete(ledStripDomain.getBrickDomain());
            outputLEDStripRepository.delete(ledStripDomain);
        }

        brickRegistry.disconnectAll();
    }


    @Test
    public void testSetColor() throws Exception {

        LOG.info("START Test testSetColor");

        for (OutputLEDStrip outputLEDStrip : outputLEDStrips) {
            outputLEDStrip.setFill(new Color(16, 32, 8));
            outputLEDStrip.updateDisplay();

            Color pixel = outputLEDStrip.getPixel(0);
            assertEquals(outputLEDStrip.getName() + " Pixel  0.red should be 16", 16, pixel.getRedAsShort());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 32", 32, pixel.getGreenAsShort());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 8", 8, pixel.getBlueAsShort());
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

            assertEquals(outputLEDStrip.getName() + " Pixel 0.red should be 0", 0, pixel0.getRedAsShort());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.green should be 0", 0, pixel0.getGreenAsShort());
            assertEquals(outputLEDStrip.getName() + " Pixel 0.blue should be 0", 0, pixel0.getBlueAsShort());
            assertEquals(outputLEDStrip.getName() + " Pixel 1.red should be 16", 16, pixel1.getRedAsShort());
            assertEquals(outputLEDStrip.getName() + " Pixel 1.green should be 35", 35, pixel1.getGreenAsShort());
            assertEquals(outputLEDStrip.getName() + " Pixel 1.blue should be 77", 77, pixel1.getBlueAsShort());
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
            assertTrue(outputLEDStrip.getName() + " Pixel 0 should be half as bright as a full white (255, 255, 255).",
                pixel.getRedAsShort() == (short) (255 * .5) && pixel.getGreenAsShort() == (short) (255 * .5)
                && pixel.getBlueAsShort() == (short) (255 * .5));
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
            assertTrue(outputLEDStrip.getName() + " Pixel 0 should be full white (255, 255, 255).",
                pixel.getRedAsShort() == 255 && pixel.getGreenAsShort() == 255 && pixel.getBlueAsShort() == 255);
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
                + " Pixel 0 should be full white (255, 255, 255).",
                pixel.getRedAsShort() == (short) (255) && pixel.getGreenAsShort() == (short) (255)
                && pixel.getBlueAsShort() == (short) (255));
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
                    pixel.getRedAsShort() == 0 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
            }

            for (int i = 5; i < 15; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRedAsShort() == 127 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
            }

            for (int i = 15; i < 30; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue("Pixel " + i + " should be black",
                    pixel.getRedAsShort() == 0 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
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
                    pixel.getRedAsShort() == 0 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
            }

            for (int i = 25; i < 30; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRedAsShort() == 127 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
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
                    pixel.getRedAsShort() == 127 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
            }

            for (int i = 5; i < 25; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be black",
                    pixel.getRedAsShort() == 0 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
            }

            for (int i = 25; i < 30; i++) {
                Color pixel = outputLEDStrip.getPixel(i);
                assertTrue(outputLEDStrip.getName() + " Pixel " + i + " should be red",
                    pixel.getRedAsShort() == 127 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
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
                    pixel.getRedAsShort() == 127 && pixel.getGreenAsShort() == 0 && pixel.getBlueAsShort() == 0);
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
