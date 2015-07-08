package org.synyx.sybil.bricklet.output.ledstrip;

import org.junit.Before;
import org.junit.Test;

import org.synyx.sybil.jenkins.domain.Status;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


public class ColorUnitTest {

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
    public void getString() throws Exception {

        Color color = new Color(1, 2, 3);

        assertThat(color.toString(), is("(1, 2, 3)"));
    }
}
