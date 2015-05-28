package org.synyx.sybil.in;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.synyx.sybil.brick.BrickRegistry;
import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;
import org.synyx.sybil.config.DevSpringConfig;
import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.InputSensorDomain;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.out.Color;
import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.OutputLEDStripRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


/**
 * IlluminanceSensorTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DevSpringConfig.class })
public class IlluminanceSensorIntegTest {

    @Autowired
    private OutputLEDStripRegistry outputLEDStripRegistry;

    @Autowired
    private OutputLEDStripRepository outputLEDStripRepository;

    @Autowired
    private BrickRepository brickRepository;

    @Autowired
    private BrickRegistry brickRegistry;

    private OutputLEDStrip ledstrip;

    private IlluminanceListener listener;

    @Before
    public void setup() {

        BrickDomain test1 = new BrickDomain("localhost", "6dLj52", 14223);

        brickRepository.save(test1);

        OutputLEDStripDomain testOne = new OutputLEDStripDomain("testone", "abc", 30, test1);

        testOne = outputLEDStripRepository.save(testOne);

        ledstrip = outputLEDStripRegistry.get(testOne);

        ledstrip.setFill(new Color(32, 0, 0));

        ledstrip.updateDisplay();

        List<String> outputs = new ArrayList<>();

        outputs.add("testone");

        InputSensorDomain sensorDomain = new InputSensorDomain("lux", "egal", SensorType.LUMINANCE, 16, 0.1, 0,
                (short) 0, outputs, test1);

        listener = new IlluminanceListener(sensorDomain, outputLEDStripRegistry, outputLEDStripRepository);
    }


    @After
    public void close() {

        ledstrip.setBrightness(1.0); // set brightness to normal
        ledstrip.setFill(Color.BLACK); // set color to black (i.e. turn all LEDs off)
        ledstrip.updateDisplay(); // make it so!

        OutputLEDStripDomain ledStripDomain = outputLEDStripRepository.findByName(ledstrip.getName());
        brickRepository.delete(ledStripDomain.getBrickDomain());
        outputLEDStripRepository.delete(ledStripDomain);

        brickRegistry.disconnectAll();
    }


    @Test
    public void testIlluminanceSensor() {

        // Control
        assertThat(ledstrip.getPixel(0), is(new Color(32, 0, 0)));

        // This shouldn't do anything yet!
        listener.illuminance(16 * 10);

        assertThat(ledstrip.getPixel(0), is(new Color(32, 0, 0)));

        // Illuminance 1 under the threshold should double the brightness
        listener.illuminance(15 * 10);

        assertThat(ledstrip.getPixel(0), is(new Color(64, 0, 0)));

        // Illuminance 0 should result in maximum brightness (but not more!)
        listener.illuminance(0);

        assertThat(ledstrip.getPixel(0), is(new Color(255, 0, 0)));
    }
}
