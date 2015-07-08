package org.synyx.sybil.bricklet.output.ledstrip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


public class Sprite1DUnitTest {

    @Test
    public void SetFill() throws Exception {

        Sprite1D sprite = new Sprite1D(3);
        sprite.setFill(new Color(64, 128, 0));

        for (int i = 0; i < 3; i++) {
            Color pixel = sprite.getPixel(i);
            assertThat(pixel.getRed(), is(64));
            assertThat(pixel.getGreen(), is(128));
            assertThat(pixel.getBlue(), is(0));
        }
    }


    @Test
    public void SetPixel() throws Exception {

        Sprite1D sprite = new Sprite1D(5);
        sprite.setPixel(0, Color.BLACK);
        sprite.setPixel(1, Color.WHITE);
        sprite.setPixel(2, new Color(64, 0, 0));
        sprite.setPixel(3, new Color(0, 64, 0));
        sprite.setPixel(4, new Color(0, 0, 64));

        int[] red = sprite.getRed();
        int[] green = sprite.getGreen();
        int[] blue = sprite.getBlue();

        assertThat(red[0], is(0));
        assertThat(green[0], is(0));
        assertThat(blue[0], is(0));

        assertThat(red[1], is(255));
        assertThat(green[1], is(255));
        assertThat(blue[1], is(255));

        assertThat(red[2], is(64));
        assertThat(green[2], is(0));
        assertThat(blue[2], is(0));

        assertThat(red[3], is(0));
        assertThat(green[3], is(64));
        assertThat(blue[3], is(0));

        assertThat(red[4], is(0));
        assertThat(green[4], is(0));
        assertThat(blue[4], is(64));
    }


    @Test
    public void getNameNoneGiven() {

        Sprite1D sprite = new Sprite1D(1);
        assertThat(sprite.getName(), is("Unnamed"));
    }


    @Test
    public void spriteFromShortListOfColors() {

        List<Color> colors = new ArrayList<>();

        colors.add(new Color(64, 0, 0));
        colors.add(new Color(0, 64, 0));
        colors.add(new Color(0, 0, 64));

        Sprite1D sprite = new Sprite1D(5, colors);

        int[] red = sprite.getRed();
        int[] green = sprite.getGreen();
        int[] blue = sprite.getBlue();

        assertThat(red.length, is(5));

        assertThat(red[0], is(64));
        assertThat(green[0], is(0));
        assertThat(blue[0], is(0));

        assertThat(red[1], is(0));
        assertThat(green[1], is(64));
        assertThat(blue[1], is(0));

        assertThat(red[2], is(0));
        assertThat(green[2], is(0));
        assertThat(blue[2], is(64));

        assertThat(red[3], is(0));
        assertThat(green[3], is(0));
        assertThat(blue[3], is(0));

        assertThat(red[4], is(0));
        assertThat(green[4], is(0));
        assertThat(blue[4], is(0));
    }


    @Test
    public void spriteFromLongListOfColors() {

        List<Color> colors = new ArrayList<>();

        colors.add(new Color(64, 0, 0));
        colors.add(new Color(0, 64, 0));
        colors.add(new Color(0, 0, 64));
        colors.add(Color.WHITE);
        colors.add(Color.WHITE);

        Sprite1D sprite = new Sprite1D(3, colors);

        int[] red = sprite.getRed();
        int[] green = sprite.getGreen();
        int[] blue = sprite.getBlue();

        assertThat(red.length, is(3));

        assertThat(red[0], is(64));
        assertThat(green[0], is(0));
        assertThat(blue[0], is(0));

        assertThat(red[1], is(0));
        assertThat(green[1], is(64));
        assertThat(blue[1], is(0));

        assertThat(red[2], is(0));
        assertThat(green[2], is(0));
        assertThat(blue[2], is(64));
    }
}
