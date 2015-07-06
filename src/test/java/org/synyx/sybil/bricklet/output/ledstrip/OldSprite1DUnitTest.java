package org.synyx.sybil.bricklet.output.ledstrip;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class OldSprite1DUnitTest {

    @Test
    public void testSetFill() throws Exception {

        OldSprite1D sprite = new OldSprite1D(3);
        sprite.setFill(new OldColor(64, 64, 0));

        for (int i = 0; i < 3; i++) {
            OldColor pixel = sprite.getPixel(i);
            assertTrue("Pixel " + i + " should be (64, 64, 0)",
                pixel.getRedAsShort() == 64 && pixel.getGreenAsShort() == 64 && pixel.getBlueAsShort() == 0);
        }
    }


    @Test
    public void testSetPixel() throws Exception {

        OldSprite1D sprite = new OldSprite1D(5);
        sprite.setPixel(0, OldColor.BLACK);
        sprite.setPixel(1, OldColor.WHITE);
        sprite.setPixel(2, new OldColor(64, 0, 0));
        sprite.setPixel(3, new OldColor(0, 64, 0));
        sprite.setPixel(4, new OldColor(0, 0, 64));

        short[] red = sprite.getRed();
        short[] green = sprite.getGreen();
        short[] blue = sprite.getBlue();

        assertThat(red[0], is((short) 0));
        assertThat(green[0], is((short) 0));
        assertThat(blue[0], is((short) 0));

        assertThat(red[1], is((short) 255));
        assertThat(green[1], is((short) 255));
        assertThat(blue[1], is((short) 255));

        assertThat(red[2], is((short) 64));
        assertThat(green[2], is((short) 0));
        assertThat(blue[2], is((short) 0));

        assertThat(red[3], is((short) 0));
        assertThat(green[3], is((short) 64));
        assertThat(blue[3], is((short) 0));

        assertThat(red[4], is((short) 0));
        assertThat(green[4], is((short) 0));
        assertThat(blue[4], is((short) 64));
    }


    @Test
    public void testGetNameNoneGiven() {

        OldSprite1D sprite = new OldSprite1D(1);
        assertEquals("Should be Unnamed", "Unnamed", sprite.getName());
    }
}
