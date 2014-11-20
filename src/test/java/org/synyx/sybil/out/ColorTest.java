package org.synyx.sybil.out;

import org.junit.Test;


public class ColorTest {

    Color color = new Color(1024, 32, -50);

    @Test
    public void testGetRed() throws Exception {

        assert (color.getRed() == 255);
    }


    @Test
    public void testGetGreen() throws Exception {

        assert (color.getGreen() == 32);
    }


    @Test
    public void testGetBlue() throws Exception {

        assert (color.getBlue() == 0);
    }
}
