package org.synyx.sybil.bricklet.output.ledstrip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;


/**
 * Shows a status as a single color on an entire LED strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class SingleStatusOnLEDStrip implements SingleStatusOutput {

    private static final Logger LOG = LoggerFactory.getLogger(SingleStatusOnLEDStrip.class);
    private final LEDStrip LEDStrip;
    private Color color;
    private Status status = Status.OKAY;
    private Color critical;
    private Color warning;
    private Color okay;

    /**
     * Instantiates a new SingleStatusOnLEDStrip with custom status colors.
     *
     * @param  LEDStrip  The output (in this case a LED strip) to show statuses on
     * @param  okay  The Color for status OKAY
     * @param  warning  The Color for status WARNING
     * @param  critical  The Color for status CRITICAL
     */
    public SingleStatusOnLEDStrip(LEDStrip LEDStrip, Color okay, Color warning, Color critical) {

        this.okay = okay;
        this.critical = critical;
        this.warning = warning;
        this.LEDStrip = LEDStrip;
    }


    /**
     * Instantiates a new SingleStatusOnLEDStrip.
     *
     * @param  LEDStrip  The output (in this case a LED strip) to show statuses on
     */
    public SingleStatusOnLEDStrip(LEDStrip LEDStrip) {

        this.LEDStrip = LEDStrip;
        critical = Color.CRITICAL;
        warning = Color.WARNING;
        okay = Color.OKAY;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SingleStatusOnLEDStrip ledStrip = (SingleStatusOnLEDStrip) o;

        return !(color != null ? !color.equals(ledStrip.color) : ledStrip.color != null)
            && !(LEDStrip != null ? !LEDStrip.equals(ledStrip.LEDStrip) : ledStrip.LEDStrip != null)
            && status == ledStrip.status;
    }


    @Override
    public int hashCode() {

        int result = LEDStrip != null ? LEDStrip.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);

        return result;
    }


    /**
     * Show the status on the LED strip.
     *
     * @param  statusInformation  The StatusInformation to display.
     */
    @Override
    public void showStatus(StatusInformation statusInformation) {

        setStatus(statusInformation);
        showStatus();
    }


    /**
     * Show status.
     */
    public void showStatus() {

        if (status == Status.CRITICAL) {
            color = critical;
        } else if (status == Status.WARNING) {
            color = warning;
        } else {
            color = okay;
        }

        LOG.debug("Set ledstrip {} to color {}", LEDStrip.getName(), color);

        LEDStrip.setFill(color);
        LEDStrip.updateDisplay();
    }


    /**
     * Gets status.
     *
     * @return  the status
     */
    public Status getStatus() {

        return status;
    }


    /**
     * Sets status.
     *
     * @param  statusInformation  the status information
     */
    public void setStatus(StatusInformation statusInformation) {

        LOG.debug("Set status {} on ledstrip {}", statusInformation.getStatus(), LEDStrip.getName());

        status = statusInformation.getStatus();
    }


    /**
     * Turn off.
     */
    public void turnOff() {

        LEDStrip.setFill(Color.BLACK);
        LEDStrip.updateDisplay();
    }
}
