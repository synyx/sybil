package org.synyx.sybil.bricklet.output.ledstrip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.util.Objects;


/**
 * Shows a status as a single color on an entire LED strip.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class SingleStatusOnLEDStrip implements SingleStatusOutput {

    private static final Logger LOG = LoggerFactory.getLogger(SingleStatusOnLEDStrip.class);
    private final LEDStrip ledStrip;
    private Color color;
    private Status status = Status.OKAY;
    private final Color critical;
    private final Color warning;
    private final Color okay;

    /**
     * Instantiates a new SingleStatusOnLEDStrip with custom status colors.
     *
     * @param  ledStrip  The output (in this case a LED strip) to show statuses on
     * @param  okay  The Color for status OKAY
     * @param  warning  The Color for status WARNING
     * @param  critical  The Color for status CRITICAL
     */
    public SingleStatusOnLEDStrip(LEDStrip ledStrip, Color okay, Color warning, Color critical) {

        this.okay = okay;
        this.critical = critical;
        this.warning = warning;
        this.ledStrip = ledStrip;
    }


    /**
     * Instantiates a new SingleStatusOnLEDStrip.
     *
     * @param  ledStrip  The output (in this case a LED strip) to show statuses on
     */
    public SingleStatusOnLEDStrip(LEDStrip ledStrip) {

        this.ledStrip = ledStrip;
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

        SingleStatusOnLEDStrip singleStatusOnLEDStrip = (SingleStatusOnLEDStrip) o;

        return Objects.equals(color, singleStatusOnLEDStrip.color)
            && Objects.equals(this.ledStrip, singleStatusOnLEDStrip.ledStrip)
            && Objects.equals(status, singleStatusOnLEDStrip.status);
    }


    @Override
    public int hashCode() {

        return Objects.hash(ledStrip, color, status);
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

        LOG.debug("Set ledstrip {} to color {}", ledStrip.getName(), color);

        ledStrip.setFill(color);
        ledStrip.updateDisplay();
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

        LOG.debug("Set status {} on ledstrip {}", statusInformation.getStatus(), ledStrip.getName());

        status = statusInformation.getStatus();
    }
}
