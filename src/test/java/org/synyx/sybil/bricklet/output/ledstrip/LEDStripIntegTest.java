package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.NotConnectedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.brick.BrickService;
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

    private List<LEDStrip> LEDStrips = new ArrayList<>();

    @Autowired
    private LEDStripRegistry LEDStripRegistry;

    @Autowired
    private LEDStripRepository LEDStripRepository;

    @Autowired
    private BrickRepository brickRepository;

    @Autowired
    private BrickService brickService;

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

        brickService.disconnectAll();
    }


    @Test
    public void testLEDStrip() {

        Sprite1D sprite = new Sprite1D(10, "10long");
        sprite.setFill(new Color(127, 0, 0));
        sprite.setPixel(8, new Color(0, 127, 0));
        sprite.setPixel(9, new Color(0, 0, 127));

        for (LEDStrip LEDStrip : LEDStrips) {
            LEDStrip.drawSprite(sprite, 25, true);
            LEDStrip.updateDisplay();

            for (int i = 0; i < 3; i++) {
                Color pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 127));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }

            Color pixel = LEDStrip.getPixel(3);
            assertThat(pixel.getRedAsShort(), is((short) 0));
            assertThat(pixel.getGreenAsShort(), is((short) 127));
            assertThat(pixel.getBlueAsShort(), is((short) 0));

            pixel = LEDStrip.getPixel(4);
            assertThat(pixel.getRedAsShort(), is((short) 0));
            assertThat(pixel.getGreenAsShort(), is((short) 0));
            assertThat(pixel.getBlueAsShort(), is((short) 127));

            for (int i = 5; i < 25; i++) {
                pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 0));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }

            for (int i = 25; i < 30; i++) {
                pixel = LEDStrip.getPixel(i);
                assertThat(pixel.getRedAsShort(), is((short) 127));
                assertThat(pixel.getGreenAsShort(), is((short) 0));
                assertThat(pixel.getBlueAsShort(), is((short) 0));
            }
        }
    }
}
