package org.synyx.sybil.out;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ColorTest {

    private Color color;

    @Before
    public void setup() {

        color = new Color(1024, 32, -50);
    }


    @Test
    public void testGetRed() throws Exception {

        assertEquals("Red should be 255", 255, color.getRedAsShort());
    }


    @Test
    public void testGetGreen() throws Exception {

        assertEquals("Green should be 32", 32, color.getGreenAsShort());
    }


    @Test
    public void testGetBlue() throws Exception {

        assertEquals("Blue should be 0", 0, color.getBlueAsShort());
    }
}
