package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


@RunWith(MockitoJUnitRunner.class)
public class LEDStripUnitTest {

    private static final Logger LOG = LoggerFactory.getLogger(LEDStripUnitTest.class);

    @Mock
    private BrickletLEDStrip brickletLEDStrip;

    private LEDStrip ledStrip;

    private InOrder inOrder;

    private short[] black = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    @Before
    public void setup() {

        ledStrip = new LEDStrip(brickletLEDStrip, 30, "mockstrip");
        inOrder = Mockito.inOrder(brickletLEDStrip);
    }


    @Test
    public void testSetFill() throws Exception {

        LOG.info("START Test testSetFill");

        ledStrip.setFill(new Color(16, 32, 8));
        ledStrip.updateDisplay();

        short[] red = new short[16];
        Arrays.fill(red, (short) 16);

        short[] green = new short[16];
        Arrays.fill(green, (short) 32);

        short[] blue = new short[16];
        Arrays.fill(blue, (short) 8);

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, blue, red, green);

        LOG.info("FINISHED Test testSetFill");
    }


    @Test
    public void testSetPixel() throws Exception {

        LOG.info("START Test testSetPixel");

        Color color = new Color(16, 35, 77);

        ledStrip.setPixelColor(1, color);
        ledStrip.updateDisplay();

        short[] red = { 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        short[] green = { 0, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        short[] blue = { 0, 77, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, black, black, black);

        LOG.info("FINISHED Test testSetPixel");
    }


    @Test
    public void testSetBrightnessHalf() throws Exception {

        LOG.info("START Test testBrightnessHalf");

        ledStrip.setFill(Color.WHITE);

        ledStrip.setBrightness(.5);
        ledStrip.updateDisplay();

        short[] white = new short[16];
        Arrays.fill(white, (short) 127);

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, white, white, white);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, white, white, white);

        LOG.info("FINISH Test testBrightnessHalf");
    }


    @Test
    public void testSetBrightnessFull() throws Exception {

        LOG.info("START Test testBrightnessFull");

        ledStrip.setFill(Color.WHITE);

        ledStrip.setBrightness(1);
        ledStrip.updateDisplay();

        short[] white = new short[16];
        Arrays.fill(white, (short) 255);

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, white, white, white);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, white, white, white);

        LOG.info("FINISH Test testBrightnessFull");
    }


    @Test
    public void testSetWhiteBrightnessDouble() throws Exception {

        LOG.info("START Test testWhiteBrightnessDouble");

        ledStrip.setFill(Color.WHITE);

        ledStrip.setBrightness(2);
        ledStrip.updateDisplay();

        short[] white = new short[16];
        Arrays.fill(white, (short) 255);

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, white, white, white);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, white, white, white);

        LOG.info("FINISH Test testWhiteBrightnessDouble");
    }


    @Test
    public void testSetBrightnessDouble() throws Exception {

        LOG.info("START Test testBrightnessDouble");

        ledStrip.setFill(new Color(2, 16, 128));

        ledStrip.setBrightness(2);
        ledStrip.updateDisplay();

        short[] red = new short[16];
        Arrays.fill(red, (short) 4);

        short[] green = new short[16];
        Arrays.fill(green, (short) 32);

        short[] blue = new short[16];
        Arrays.fill(blue, (short) 255);

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, blue, red, green);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, blue, red, green);

        LOG.info("FINISH Test testBrightnessDouble");
    }


    @Test
    public void testDrawSprite() throws Exception {

        LOG.info("START Test testDrawSprite");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        ledStrip.drawSprite(sprite, 5);
        ledStrip.updateDisplay();

        short[] red = { 0, 0, 0, 0, 0, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 0 };

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, black, red, black);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, black, black, black);

        LOG.info("FINISH Test testDrawSprite");
    }


    @Test
    public void testDrawSpriteWithoutWrap() throws Exception {

        LOG.info("START Test testDrawSpriteWithoutWrap");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        ledStrip.drawSprite(sprite, 25);
        ledStrip.updateDisplay();

        short[] red = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 127, 127, 127, 127, 0, 0 };

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, black, black, black);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, black, red, black);

        LOG.info("FINISH Test testDrawSpriteWithoutWrap");
    }


    @Test
    public void testDrawSpriteWithWrap() throws Exception {

        LOG.info("START Test testDrawSpriteWithWrap");

        Sprite1D sprite = new Sprite1D(10, "10 red");
        sprite.setFill(new Color(127, 0, 0));

        ledStrip.drawSpriteWithWrap(sprite, 25);
        ledStrip.updateDisplay();

        short[] red1 = { 127, 127, 127, 127, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        short[] red2 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 127, 127, 127, 127, 0, 0 };

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, black, red1, black);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, black, red2, black);

        LOG.info("FINISH Test testDrawSpriteWithWrap");
    }


    @Test
    public void testDrawSpriteOverlong() throws Exception {

        LOG.info("START Test testDrawSpriteOverlong");

        Sprite1D sprite = new Sprite1D(50, "50 red");
        sprite.setFill(new Color(127, 0, 0));

        ledStrip.drawSpriteWithWrap(sprite, 25);
        ledStrip.updateDisplay();

        short[] red1 = new short[16];
        Arrays.fill(red1, (short) 127);

        short[] red2 = { 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 0, 0 };

        inOrder.verify(brickletLEDStrip).setRGBValues(0, (short) 16, black, red1, black);
        inOrder.verify(brickletLEDStrip).setRGBValues(16, (short) 16, black, red2, black);

        LOG.info("FINISH Test testDrawSpriteOverlong");
    }


    @Test
    public void testGetLength() {

        LOG.info("START Test testGetLength");

        assertThat(ledStrip.getLength(), is(30));

        LOG.info("FINISH Test testGetLength");
    }
}
