package org.synyx.sybil.out;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class OutputLEDStripTest {

    private IPConnection ipConnection;
    private BrickletLEDStrip ledStrip;

    @Before
    public void setup() throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

        ipConnection = new IPConnection();

        ipConnection.connect("localhost", 4223);

        ledStrip = new BrickletLEDStrip("p3c", ipConnection);

        ledStrip.setChipType(2812);

        ledStrip.setFrameDuration(10);
    }


    @After
    public void close() throws NotConnectedException {

        if (ipConnection != null) {
            ipConnection.disconnect();
        }
    }


    @Test
    public void testSetColor() throws Exception {

        OutputLEDStrip outputLEDStrip = new OutputLEDStrip(ledStrip, 30);

        outputLEDStrip.setColor(new Color(16, 32, 8));

        assert (outputLEDStrip.getPixel(0).getRed() == 16);
        assert (outputLEDStrip.getPixel(0).getGreen() == 32);
        assert (outputLEDStrip.getPixel(0).getBlue() == 8);

        outputLEDStrip.setColor(new Color(0, 0, 0));
    }


    @Test
    public void testSetPixel() throws Exception {

        OutputLEDStrip outputLEDStrip = new OutputLEDStrip(ledStrip, 30);

        Color black = new Color(0, 0, 0);
        Color red = new Color(16, 0, 0);

//        System.out.println("Setting to black");
        outputLEDStrip.setColor(black);
        assert (outputLEDStrip.getPixel(18).getBlue() == 0);

//        System.out.println("Setting pixel 18 to red");
        outputLEDStrip.setPixel(18, red);
        assert (outputLEDStrip.getPixel(18).getRed() == 16);

//        System.out.println("Setting to black");
        outputLEDStrip.setColor(black);

        for (int i = 0; i < 30; i++) {
            outputLEDStrip.setPixel(i, red);
//            Thread.sleep(100);
//            System.out.println(outputLEDStrip.getPixel(i).getRed());
        }

        for (int i = 0; i < 30; i++) {
            assert (outputLEDStrip.getPixel(i).getRed() == 16);
        }

        outputLEDStrip.setColor(black);
    }
}
