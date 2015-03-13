package org.synyx.sybil.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;


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

    /**
     * Constructor.
     *
     * @param  outputLEDStrip  The output (in this case a LED strip) to show statuses on
     */
    public SingleStatusOnLEDStrip(OutputLEDStrip outputLEDStrip) {

        this.outputLEDStrip = outputLEDStrip;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        SingleStatusOnLEDStrip ledStrip = (SingleStatusOnLEDStrip) o;

        if (color != null ? !color.equals(ledStrip.color) : ledStrip.color != null)
            return false;

        if (outputLEDStrip != null ? !outputLEDStrip.equals(ledStrip.outputLEDStrip) : ledStrip.outputLEDStrip != null)
            return false;

        if (status != ledStrip.status)
            return false;

        return true;
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


    public void showStatus() {

        if (status == Status.CRITICAL) {
            color = Color.CRITICAL;
        } else if (status == Status.WARNING) {
            color = Color.WARNING;
        } else {
            color = Color.OKAY;
        }

        LOG.debug("Set ledstrip {} to color {}", outputLEDStrip.getName(), color);

        outputLEDStrip.setFill(color);
        outputLEDStrip.updateDisplay();
    }


    public Status getStatus() {

        return status;
    }


    public void setStatus(StatusInformation statusInformation) {

        LOG.debug("Show status {} on ledstrip {}", statusInformation.getStatus(), outputLEDStrip.getName());

        status = statusInformation.getStatus();
    }


    public void turnOff() {

        outputLEDStrip.setFill(Color.BLACK);
        outputLEDStrip.updateDisplay();
    }
}
