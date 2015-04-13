package org.synyx.sybil.out;

import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.common.Bricklet;


/**
 * Controls a dual bricklet bricklet.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class OutputRelay implements Bricklet {

    private static final Logger LOG = LoggerFactory.getLogger(OutputRelay.class);
    private final BrickletDualRelay bricklet;
    private String name;

    /**
     * Makes new OutputRelay object.
     *
     * @param  bricklet  The bricklet brick we want to control.
     * @param  name  The name to address the bricklet with, always lowercase!
     */
    public OutputRelay(BrickletDualRelay bricklet, String name) {

        this.name = name.toLowerCase();

        this.bricklet = bricklet;

        LOG.debug("Creating new OutputRelay {}", name);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        OutputRelay that = (OutputRelay) o;

        return name.equals(that.name);
    }


    @Override
    public int hashCode() {

        int result = bricklet.hashCode();
        result = 31 * result + name.hashCode();

        return result;
    }


    @Override
    public String getName() {

        return name;
    }


    /**
     * Set the state of one of the two relays.
     *
     * @param  relay  The relay: Either ONE or TWO.
     * @param  state  The state: true (on) or false (off).
     */
    public void setState(EnumRelay relay, boolean state) {

        short selector = relay.getValue();

        try {
            bricklet.setSelectedState(selector, state);
            LOG.debug("Set relay {} of {} to: {}.", selector, this.name, state);
        } catch (TimeoutException | NotConnectedException e) {
            LOG.error("Error connecting to relay {} while setting state: {}", name, e.toString());
        }
    }


    /**
     * Sets states of both relays.
     *
     * @param  stateOne  The state of relay one.
     * @param  stateTwo  The state of relay two.
     */
    public void setStates(boolean stateOne, boolean stateTwo) {

        try {
            bricklet.setState(stateOne, stateTwo);
            LOG.debug("Set relays of {} to: {}, {}.", this.name, stateOne, stateTwo);
        } catch (TimeoutException | NotConnectedException e) {
            LOG.error("Error connecting to relay {} while setting states: {}", name, e.toString());
        }
    }


    /**
     * Get the state of one of the two relays.
     *
     * @param  relay  The relay: Either 1 or 2.
     *
     * @return  the state
     */
    public boolean getState(EnumRelay relay) {

        BrickletDualRelay.State state = null;

        try {
            state = bricklet.getState();
            LOG.debug("Getting states of relay {}: {}, {}", this.name, state.relay1, state.relay2);
        } catch (TimeoutException | NotConnectedException e) {
            LOG.error("Error connecting to relay {} while getting state: {}", name, e.toString());
        }

        if (state != null) {
            switch (relay) {
                case ONE:
                    return state.relay1;

                case TWO:
                    return state.relay2;
            }
        }

        return false;
    }


    /**
     * Get the states of both relays.
     *
     * @return  The boolean array with two elements.
     */
    public boolean[] getStates() {

        BrickletDualRelay.State state = null;

        try {
            state = bricklet.getState();
            LOG.debug("Getting states of relay {}: {}, {}", this.name, state.relay1, state.relay2);
        } catch (TimeoutException | NotConnectedException e) {
            LOG.error("Error connecting to relay {} while getting state: {}", name, e.toString());
        }

        if (state != null) {
            boolean[] result = new boolean[2];

            result[0] = state.relay1;
            result[1] = state.relay2;

            return result;
        }

        return new boolean[] { false, false };
    }
}
