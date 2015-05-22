package org.synyx.sybil.in;

import com.tinkerforge.BrickletIO4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.database.OutputRelayRepository;
import org.synyx.sybil.domain.InputSensorDomain;
import org.synyx.sybil.domain.OutputRelayDomain;
import org.synyx.sybil.out.OutputRelay;
import org.synyx.sybil.out.OutputRelayRegistry;

import java.util.ArrayList;
import java.util.List;


/**
 * ButtonListener.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class ButtonListener implements BrickletIO4.InterruptListener {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonListener.class);

    private short pins;

    private List<OutputRelay> relays = new ArrayList<>();

    public ButtonListener(InputSensorDomain sensor, OutputRelayRegistry outputRelayRegistry,
        OutputRelayRepository outputRelayRepository) {

        LOG.debug("Listener added to {}", sensor.getName());

        pins = sensor.getPins();

        for (String output : sensor.getOutputs()) {
            OutputRelayDomain domain = outputRelayRepository.findByName(output);

            if (domain != null) {
                OutputRelay relay = outputRelayRegistry.get(domain);
                relays.add(relay);
            } else {
                LOG.error("Configured output {} of button {} does not match a relay.", output, sensor.getName());
                HealthController.setHealth(Status.WARNING, "ButtonListener");
            }
        }
    }

    @Override
    public void interrupt(short interruptMask, short valueMask) {

        if ((interruptMask & pins) > 0) { // if at least one of the configured pins has an interrupt

            if ((~valueMask & pins) > 0) { // if at least one of the configured pins has been pulled to ground (i.e. is 0)

                for (OutputRelay relay : relays) {
                    relay.setStates(false, false); // turn all the outputs off
                }
            }
        }
    }
}
