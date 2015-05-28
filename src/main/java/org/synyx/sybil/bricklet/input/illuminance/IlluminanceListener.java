package org.synyx.sybil.bricklet.input.illuminance;

import com.tinkerforge.BrickletAmbientLight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.bricklet.input.database.InputSensorDomain;
import org.synyx.sybil.bricklet.output.ledstrip.OutputLEDStrip;
import org.synyx.sybil.bricklet.output.ledstrip.OutputLEDStripRegistry;
import org.synyx.sybil.bricklet.output.ledstrip.database.OutputLEDStripDomain;
import org.synyx.sybil.bricklet.output.ledstrip.database.OutputLEDStripRepository;
import org.synyx.sybil.in.Status;

import java.util.ArrayList;
import java.util.List;


/**
 * IlluminanceListener.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceListener implements BrickletAmbientLight.IlluminanceListener {

    private static final Logger LOG = LoggerFactory.getLogger(IlluminanceListener.class);

    private List<OutputLEDStrip> ledStrips = new ArrayList<>();

    private int threshold;

    private double multiplier;

    public IlluminanceListener(InputSensorDomain sensor, OutputLEDStripRegistry outputLEDStripRegistry,
        OutputLEDStripRepository outputLEDStripRepository) {

        LOG.debug("Listener added to {}", sensor.getName());

        threshold = sensor.getThreshold();

        multiplier = sensor.getMultiplier();

        for (String output : sensor.getOutputs()) {
            OutputLEDStripDomain domain = outputLEDStripRepository.findByName(output);

            if (domain != null) {
                OutputLEDStrip ledStrip = outputLEDStripRegistry.get(domain);
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

        for (OutputLEDStrip ledStrip : ledStrips) {
            if (brightness != ledStrip.getBrightness()) {
                ledStrip.setBrightness(brightness);
                ledStrip.updateDisplay();
            }
        }
    }
}
