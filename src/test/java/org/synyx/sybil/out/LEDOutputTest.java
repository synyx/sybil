package org.synyx.sybil.out;

import org.junit.Test;


public class LEDOutputTest {

    @Test
    public void testSetColor() throws Exception {

        LEDOutput ledOutput = new LEDOutput("localhost", 4223, "p3c", 2812, 10, 30);

        ledOutput.setColor(new Color(16, 32, 8));

        assert (ledOutput.getPixel(0).getRed() == 16);
        assert (ledOutput.getPixel(0).getGreen() == 32);
        assert (ledOutput.getPixel(0).getBlue() == 8);

        ledOutput.setColor(new Color(0, 0, 0));
        ledOutput.close();
    }


    @Test
    public void testSetPixel() throws Exception {

        LEDOutput ledOutput = new LEDOutput("localhost", 4223, "p3c", 2812, 10, 30);

        Color black = new Color(0, 0, 0);
        Color red = new Color(16, 0, 0);

//        System.out.println("Setting to black");
        ledOutput.setColor(black);
        assert (ledOutput.getPixel(18).getBlue() == 0);
//        System.out.println(ledOutput.getPixel(18).getRed());

        System.out.println("Setting pixel 18 to red");
        ledOutput.setPixel(18, red);
        assert (ledOutput.getPixel(18).getRed() == 16);
//        System.out.println(ledOutput.getPixel(18).getRed());

//        System.out.println("Setting to black");
        ledOutput.setColor(black);

        for (int i = 0; i < 30; i++) {
            ledOutput.setPixel(i, red);
//            Thread.sleep(100);
//            System.out.println(ledOutput.getPixel(i).getRed());
        }

        for (int i = 0; i < 30; i++) {
            assert (ledOutput.getPixel(i).getRed() == 16);
        }

        ledOutput.setColor(black);
    }
}
