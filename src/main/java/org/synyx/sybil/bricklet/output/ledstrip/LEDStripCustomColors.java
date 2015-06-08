package org.synyx.sybil.bricklet.output.ledstrip;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * LEDStripCustomColors.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class LEDStripCustomColors {

    private Map<String, Map<String, Color>> customStatusColors = new HashMap<>();

    public void put(String name, Map<String, Color> colors) {

        customStatusColors.put(name, colors);
    }


    public Map<String, Color> get(String name) {

        return customStatusColors.get(name);
    }


    public void clear() {

        customStatusColors.clear();
    }
}
