package org.synyx.sybil.bricklet.output.ledstrip;

import com.tinkerforge.BrickletLEDStrip;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.jenkins.domain.Status;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


@RunWith(MockitoJUnitRunner.class)
public class ColorUnitTest {

    @Mock
    BrickletLEDStrip.RGBValues rgbValues;

    private Color color;

    @Before
    public void setup() {

        color = new Color(1024, 32, -50);
    }


    @Test
    public void getRed() throws Exception {

        assertThat(color.getRed(), is(255));
    }


    @Test
    public void getGreen() throws Exception {

        assertThat(color.getGreen(), is(32));
    }


    @Test
    public void getBlue() throws Exception {

        assertThat(color.getBlue(), is(0));
    }


    @Test
    public void colorFromStatus() throws Exception {

        assertThat(Color.colorFromStatus(Status.OKAY), is(Color.OKAY));
        assertThat(Color.colorFromStatus(Status.WARNING), is(Color.WARNING));
        assertThat(Color.colorFromStatus(Status.CRITICAL), is(Color.CRITICAL));
    }


    @Test
    public void isEqual() throws Exception {

        Color color = new Color(1, 2, 3);
        Color sameColor = new Color(1, 2, 3);

        assert (color.equals(sameColor));
    }


    @Test
    public void isNotEqual() throws Exception {

        Color color = new Color(1, 2, 3);
        Color sameColor = new Color(3, 2, 1);

        assert (!color.equals(sameColor));
    }


    @Test
    public void getString() throws Exception {

        Color color = new Color(1, 2, 3);

        assertThat(color.toString(), is("(1, 2, 3)"));
    }


    @Test
    public void colorFromLEDStrip() throws Exception {

        // setup
        short[] r = { 255, 0, 0 };
        short[] g = { 0, 255, 0 };
        short[] b = { 0, 0, 255 };

        // WS2812 use BRG instead of RGB
        rgbValues.r = b;
        rgbValues.g = r;
        rgbValues.b = g;

        // execution
        Color color = Color.colorFromLedStrip(rgbValues);

        // verification
        assertThat(color, is(new Color(255, 0, 0)));
    }
}
