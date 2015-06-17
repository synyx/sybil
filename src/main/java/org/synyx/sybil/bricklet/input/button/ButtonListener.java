package org.synyx.sybil.bricklet.input.button;

import com.tinkerforge.BrickletIO4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.api.HealthController;
import org.synyx.sybil.bricklet.input.button.database.ButtonDomain;
import org.synyx.sybil.bricklet.output.relay.Relay;
import org.synyx.sybil.bricklet.output.relay.RelayService;
import org.synyx.sybil.bricklet.output.relay.database.RelayDomain;
import org.synyx.sybil.jenkins.domain.Status;

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

    private List<Relay> relays = new ArrayList<>();

    public ButtonListener(ButtonDomain sensor, RelayService relayService) {

        LOG.debug("Listener added to {}", sensor.getName());

        pins = sensor.getPins();

        for (String output : sensor.getOutputs()) {
            RelayDomain domain = relayService.getDomain(output);

            if (domain != null) {
                Relay relay = relayService.getRelay(domain);
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

                for (Relay relay : relays) {
                    relay.setStates(false, false); // turn all the outputs off
                }
            }
        }
    }
}
