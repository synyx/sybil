package org.synyx.sybil.out;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class Sprite1DTest {

    @Test
    public void testSetFill() throws Exception {

        Sprite1D sprite = new Sprite1D(3);
        sprite.setFill(new Color(64, 64, 0));

        for (int i = 0; i < 3; i++) {
            Color pixel = sprite.getPixel(i);
            assertTrue("Pixel " + i + " should be (64, 64, 0)",
                pixel.getRedAsShort() == 64 && pixel.getGreenAsShort() == 64 && pixel.getBlueAsShort() == 0);
        }
    }


    @Test
    public void testSetPixel() throws Exception {

        Sprite1D sprite = new Sprite1D(5);
        sprite.setPixel(0, Color.BLACK);
        sprite.setPixel(1, Color.WHITE);
        sprite.setPixel(2, new Color(64, 0, 0));
        sprite.setPixel(3, new Color(0, 64, 0));
        sprite.setPixel(4, new Color(0, 0, 64));

        short[] red = sprite.getRed();
        short[] green = sprite.getGreen();
        short[] blue = sprite.getBlue();

        assertTrue("Pixel 0 should be black", red[0] == 0 && green[0] == 0 && blue[0] == 0);
        assertTrue("Pixel 1 should be white", red[1] == 127 && green[1] == 127 && blue[1] == 127);
        assertTrue("Pixel 2 should be red", red[2] == 64 && green[2] == 0 && blue[2] == 0);
        assertTrue("Pixel 3 should be green", red[3] == 0 && green[3] == 64 && blue[3] == 0);
        assertTrue("Pixel 4 should be blue", red[4] == 0 && green[4] == 0 && blue[4] == 64);
    }


    @Test
    public void testGetNameNoneGiven() {

        Sprite1D sprite = new Sprite1D(1);
        assertEquals("Should be Unnamed", "Unnamed", sprite.getName());
    }
}
