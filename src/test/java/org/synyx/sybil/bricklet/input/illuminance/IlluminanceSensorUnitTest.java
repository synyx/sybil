package org.synyx.sybil.bricklet.input.illuminance;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * IlluminanceSensorTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class IlluminanceSensorUnitTest {

    @Mock
    private LEDStripRegistry ledStripRegistry;

    @Mock
    private LEDStripRepository ledStripRepository;

    @Mock
    private LEDStrip ledstrip;

    private IlluminanceListener listener;

    @Before
    public void setup() {

        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);

        LEDStripDomain testOne = new LEDStripDomain("testone", "abc", 30, test1);

        Mockito.when(ledStripRepository.findByName("testone")).thenReturn(testOne);

        Mockito.when(ledStripRegistry.get(testOne)).thenReturn(ledstrip);

        List<String> outputs = new ArrayList<>();

        outputs.add("testone");

        IlluminanceSensorDomain sensorDomain = new IlluminanceSensorDomain("lux", "egal", 16, 0.1, outputs, test1);

        listener = new IlluminanceListener(sensorDomain, ledStripRegistry, ledStripRepository);
    }


    @Test
    public void testIlluminanceSensor() {

        InOrder inOrder = Mockito.inOrder(ledstrip);

        // This shouldn't do anything yet!
        listener.illuminance(16 * 10);

        inOrder.verify(ledstrip).setBrightness(1.0);
        inOrder.verify(ledstrip).updateDisplay();

        // Illuminance 1 under the threshold should double the brightness
        listener.illuminance(15 * 10);

        inOrder.verify(ledstrip).setBrightness(2.0);
        inOrder.verify(ledstrip).updateDisplay();

        // Illuminance 0 should result in brightness threshold + 1
        listener.illuminance(0);

        inOrder.verify(ledstrip).setBrightness(17.0);
        inOrder.verify(ledstrip).updateDisplay();
    }
}
