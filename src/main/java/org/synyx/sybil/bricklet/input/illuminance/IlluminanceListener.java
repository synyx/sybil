package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.BrickletAmbientLight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripRepository;
import org.synyx.sybil.jenkins.domain.Status;

import java.util.ArrayList;
import java.util.List;


/**
 * IlluminanceListener.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceListener implements BrickletAmbientLight.IlluminanceListener {

    private static final Logger LOG = LoggerFactory.getLogger(IlluminanceListener.class);

    private List<LEDStrip> ledStrips = new ArrayList<>();

    private int threshold;

    private double multiplier;

    public IlluminanceListener(IlluminanceSensorDomain sensor, LEDStripRegistry LEDStripRegistry,
        LEDStripRepository LEDStripRepository) {

        LOG.debug("Listener added to {}", sensor.getName());

        threshold = sensor.getThreshold();

        multiplier = sensor.getMultiplier();

        for (String output : sensor.getOutputs()) {
            LEDStripDomain domain = LEDStripRepository.findByName(output);

            if (domain != null) {
                LEDStrip ledStrip = LEDStripRegistry.get(domain);
                ledStrips.add(ledStrip);
            } else {
                LOG.error("Configured output {} of illuminance sensor {} does not match a LED Strip.", output,
                    sensor.getName());
                HealthController.setHealth(Status.WARNING, "IlluminanceListener");
            }
        }
    }

    @Override
    public void illuminance(int illuminance) {

        // 1 lux is 10 units from the sensor.
        LOG.debug("Lux: {}", illuminance / 10);

        double brightness = 1.0;

        // Threshold is configured in lux!
        if (illuminance < threshold * 10) {
            brightness += ((threshold * 10) - illuminance) * multiplier;
        }

        for (LEDStrip ledStrip : ledStrips) {
            if (brightness != ledStrip.getBrightness()) {
                ledStrip.setBrightness(brightness);
                ledStrip.updateDisplay();
            }
        }
    }
}
