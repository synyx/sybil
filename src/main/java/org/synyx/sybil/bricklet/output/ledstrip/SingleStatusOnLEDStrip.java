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
    private final OutputLEDStrip outputLEDStrip;
    private Color color;
    private Status status = Status.OKAY;
    private Color critical;
    private Color warning;
    private Color okay;

    /**
     * Instantiates a new SingleStatusOnLEDStrip with custom status colors.
     *
     * @param  outputLEDStrip  The output (in this case a LED strip) to show statuses on
     * @param  okay  The Color for status OKAY
     * @param  warning  The Color for status WARNING
     * @param  critical  The Color for status CRITICAL
     */
    public SingleStatusOnLEDStrip(OutputLEDStrip outputLEDStrip, Color okay, Color warning, Color critical) {

        this.okay = okay;
        this.critical = critical;
        this.warning = warning;
        this.outputLEDStrip = outputLEDStrip;
    }


    /**
     * Instantiates a new SingleStatusOnLEDStrip.
     *
     * @param  outputLEDStrip  The output (in this case a LED strip) to show statuses on
     */
    public SingleStatusOnLEDStrip(OutputLEDStrip outputLEDStrip) {

        this.outputLEDStrip = outputLEDStrip;
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
            && !(outputLEDStrip != null ? !outputLEDStrip.equals(ledStrip.outputLEDStrip)
                                        : ledStrip.outputLEDStrip != null) && status == ledStrip.status;
    }


    @Override
    public int hashCode() {

        int result = outputLEDStrip != null ? outputLEDStrip.hashCode() : 0;
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

        LOG.debug("Set ledstrip {} to color {}", outputLEDStrip.getName(), color);

        outputLEDStrip.setFill(color);
        outputLEDStrip.updateDisplay();
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

        LOG.debug("Set status {} on ledstrip {}", statusInformation.getStatus(), outputLEDStrip.getName());

        status = statusInformation.getStatus();
    }


    /**
     * Turn off.
     */
    public void turnOff() {

        outputLEDStrip.setFill(Color.BLACK);
        outputLEDStrip.updateDisplay();
    }
}
