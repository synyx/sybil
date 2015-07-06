package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.BrickletAmbientLight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.bricklet.input.illuminance.database.IlluminanceSensorDomain;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.OldLEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.database.OLdLEDStripDomain;
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
    private static final int BY_TEN = 10;
    private static final double ONE_POINT_ZERO = 1.0;

    private List<LEDStrip> ledStrips = new ArrayList<>();
    private int threshold;
    private double multiplier;

    public IlluminanceListener(IlluminanceSensorDomain sensor, OldLEDStripService ledStripService) {

        LOG.debug("Listener added to {}", sensor.getName());

        threshold = sensor.getThreshold();

        multiplier = sensor.getMultiplier();

        for (String output : sensor.getOutputs()) {
            OLdLEDStripDomain domain = ledStripService.getDomain(output);

            if (domain != null) {
                LEDStrip ledStrip = ledStripService.getLEDStrip(domain);
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
        LOG.debug("Lux: {}", illuminance / BY_TEN);

        double brightness = ONE_POINT_ZERO;

        // Threshold is configured in lux!
        if (illuminance < threshold * BY_TEN) {
            brightness += ((threshold * BY_TEN) - illuminance) * multiplier;
        }

        for (LEDStrip ledStrip : ledStrips) {
            if (brightness != ledStrip.getBrightness()) {
                ledStrip.setBrightness(brightness);
                ledStrip.updateDisplay();
            }
        }
    }
}
