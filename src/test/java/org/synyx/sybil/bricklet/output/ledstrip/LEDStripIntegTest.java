package org.synyx.sybil.bricklet.output.ledstrip;

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

import org.synyx.sybil.brick.BrickRegistry;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
import org.synyx.sybil.config.DevSpringConfig;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class LEDStripIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(LEDStripIntegTest.class);

    private List<LEDStrip> LEDStrips = new ArrayList<>();

    @Autowired
    private LEDStripRegistry LEDStripRegistry;

    @Autowired
    private LEDStripRepository LEDStripRepository;

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
        LEDStripDomain testOne = new LEDStripDomain("testone", "abc", 30, test1);
        LEDStripDomain testTwo = new LEDStripDomain("testtwo", "def", 30, test2);
        LEDStripDomain testThree = new LEDStripDomain("testthree", "ghi", 30, test3);

        // add them to the database
        testOne = LEDStripRepository.save(testOne);
        testTwo = LEDStripRepository.save(testTwo);
        testThree = LEDStripRepository.save(testThree);

        // initialise LED Strips (fetching them from the database on the way), cast and add them to the list
        LEDStrips.add(LEDStripRegistry.get(testOne));
        LEDStrips.add(LEDStripRegistry.get(testTwo));
        LEDStrips.add(LEDStripRegistry.get(testThree));
    }


    @After
    public void close() throws NotConnectedException {

        for (LEDStrip LEDStrip : LEDStrips) { // iterate over list of strips

            LEDStrip.setBrightness(1.0); // set brightness to normal
            LEDStrip.setFill(Color.BLACK); // set color to black (i.e. turn all LEDs off)
            LEDStrip.updateDisplay(); // make it so!

            LEDStripDomain ledStripDomain = LEDStripRepository.findByName(LEDStrip.getName());
            brickRepository.delete(ledStripDomain.getBrickDomain());
            LEDStripRepository.delete(ledStripDomain);
        }

        brickRegistry.disconnectAll();
    }


    @Test
    public void testSetFill() throws Exception {

        LOG.info("START Test testSetFill");

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.setFill(new Color(16, 32, 8));
            LEDStrip.updateDisplay();

            Color pixel = LEDStrip.getPixel(0);
            assertThat(pixel.getRedAsShort(), is((short) 16));
            assertThat(pixel.getGreenAsShort(), is((short) 32));
            assertThat(pixel.getBlueAsShort(), is((short) 8));
        }

        LOG.info("FINISHED Test testSetFill");
    }


    @Test
    public void testSetPixel() throws Exception {

        LOG.info("START Test testSetPixel");

        Color color = new Color(16, 35, 77);

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.setPixel(1, color);
            LEDStrip.updateDisplay();

            Color pixel0 = LEDStrip.getPixel(0);
            Color pixel1 = LEDStrip.getPixel(1);

            assertThat(pixel0.getRedAsShort(), is((short) 0));
            assertThat(pixel0.getGreenAsShort(), is((short) 0));
            assertThat(pixel0.getBlueAsShort(), is((short) 0));
            assertThat(pixel1.getRedAsShort(), is((short) 16));
            assertThat(pixel1.getGreenAsShort(), is((short) 35));
            assertThat(pixel1.getBlueAsShort(), is((short) 77));
        }

        LOG.info("FINISHED Test testSetPixel");
    }


    @Test
    public void testSetBrightnessHalf() throws Exception {

        LOG.info("START Test testBrightnessHalf");

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.setFill(Color.WHITE);

            LEDStrip.setBrightness(.5);
            LEDStrip.updateDisplay();

            Color pixel = LEDStrip.getPixel(0);
            assertThat(pixel.getRedAsShort(), is((short) (255 * .5)));
            assertThat(pixel.getGreenAsShort(), is((short) (255 * .5)));
            assertThat(pixel.getBlueAsShort(), is((short) (255 * .5)));
        }

        LOG.info("FINISH Test testBrightnessHalf");
    }


    @Test
    public void testSetBrightnessFull() throws Exception {

        LOG.info("START Test testBrightnessFull");

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.setFill(Color.WHITE);

            LEDStrip.setBrightness(1);
            LEDStrip.updateDisplay();

            Color pixel = LEDStrip.getPixel(0);
            assertThat(pixel.getRedAsShort(), is((short) 255));
            assertThat(pixel.getGreenAsShort(), is((short) 255));
            assertThat(pixel.getBlueAsShort(), is((short) 255));
        }

        LOG.info("FINISH Test testBrightnessFull");
    }


    @Test
    public void testSetWhiteBrightnessDouble() throws Exception {

        LOG.info("START Test testWhiteBrightnessDouble");

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.setFill(Color.WHITE);

            LEDStrip.setBrightness(2);
            LEDStrip.updateDisplay();

            Color pixel = LEDStrip.getPixel(0);
            assertThat(pixel.getRedAsShort(), is((short) 255));
            assertThat(pixel.getGreenAsShort(), is((short) 255));
            assertThat(pixel.getBlueAsShort(), is((short) 255));
        }

        LOG.info("FINISH Test testWhiteBrightnessDouble");
    }


    @Test
    public void testSetBrightnessDouble() throws Exception {

        LOG.info("START Test testBrightnessDouble");

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.setFill(new Color(2, 16, 128));

            LEDStrip.setBrightness(2);
            LEDStrip.updateDisplay();

            Color pixel = LEDStrip.getPixel(0);
            assertThat(pixel.getRedAsShort(), is((short) 4));
            assertThat(pixel.getGreenAsShort(), is((short) 32));
            assertThat(pixel.getBlueAsShort(), is((short) 255));
        }

        LOG.info("FINISH Test testBrightnessDouble");
    }


    @Test
    public void testDrawSprite() {

        LOG.info("START Test testDrawSprite");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.drawSprite(sprite, 5);
            LEDStrip.updateDisplay();

            for (int i = 0; i < 5; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 0));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }

            for (int i = 5; i < 15; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 127));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }

            for (int i = 15; i < 30; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 0));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }
        }

        LOG.info("FINISH Test testDrawSprite");
    }


    @Test
    public void testDrawSpriteWithoutWrap() {

        LOG.info("START Test testDrawSpriteWithoutWrap");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.drawSprite(sprite, 25);
            LEDStrip.updateDisplay();

            for (int i = 0; i < 25; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 0));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }

            for (int i = 25; i < 30; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 127));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }
        }

        LOG.info("FINISH Test testDrawSpriteWithoutWrap");
    }


    @Test
    public void testDrawSpriteWithWrap() {

        LOG.info("START Test testDrawSpriteWithWrap");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.drawSprite(sprite, 25, true);
            LEDStrip.updateDisplay();

            for (int i = 0; i < 5; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 127));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }

            for (int i = 5; i < 25; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 0));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }

            for (int i = 25; i < 30; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 127));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }
        }

        LOG.info("FINISH Test testDrawSpriteWithWrap");
    }


    @Test
    public void testDrawSpriteOverlong() {

        LOG.info("START Test testDrawSpriteOverlong");

        Sprite1D sprite = new Sprite1D(50, "50 red");
        sprite.setFill(new Color(127, 0, 0));

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.drawSprite(sprite, 25, true);
            LEDStrip.updateDisplay();

            for (int i = 0; i < 30; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 127));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }
        }

        LOG.info("FINISH Test testDrawSpriteOverlong");
    }


    @Test
    public void testGetLength() {

        LOG.info("START Test testGetLength");

        for (LEDStrip LEDStrip : LEDStrips) {
            assertThat(LEDStrip.getLength(), is(LEDStripRepository.findByName(LEDStrip.getName()).getLength()));
        }

        LOG.info("FINISH Test testGetLength");
    }
}
