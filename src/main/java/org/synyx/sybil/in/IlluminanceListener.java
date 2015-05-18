package org.synyx.sybil.in;

import com.tinkerforge.BrickletAmbientLight;

import org.synyx.sybil.database.OutputLEDStripRepository;
import org.synyx.sybil.domain.OutputLEDStripDomain;
import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.OutputLEDStripRegistry;

import java.util.ArrayList;
import java.util.List;


/**
 * IlluminanceListener.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceListener implements BrickletAmbientLight.IlluminanceListener {

    private List<OutputLEDStrip> ledStrips = new ArrayList<>();

    public IlluminanceListener(List<String> outputs, OutputLEDStripRegistry outputLEDStripRegistry,
        OutputLEDStripRepository outputLEDStripRepository) {

        for (String output : outputs) {
            OutputLEDStripDomain domain = outputLEDStripRepository.findByName(output);

            OutputLEDStrip ledStrip = outputLEDStripRegistry.get(domain);

            ledStrips.add(ledStrip);
        }
    }

    @Override
    public void illuminance(int illuminance) {

        int lux = illuminance / 10;

        int threshold = 16;

        if (lux < threshold) {
            int brightness = threshold - lux;

            for (OutputLEDStrip ledStrip : ledStrips) {
                ledStrip.setBrightness(brightness);
            }
        }
    }
}
