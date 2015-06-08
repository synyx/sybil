package org.synyx.sybil.bricklet.output.ledstrip;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


public class ColorUnitTest {

    private Color color;

    @Before
    public void setup() {

        color = new Color(1024, 32, -50);
    }


    @Test
    public void testGetRed() throws Exception {

        assertThat(color.getRedAsShort(), is((short) 255));
    }


    @Test
    public void testGetGreen() throws Exception {

        assertThat(color.getGreenAsShort(), is((short) 32));
    }


    @Test
    public void testGetBlue() throws Exception {

        assertThat(color.getBlueAsShort(), is((short) 0));
    }
}
