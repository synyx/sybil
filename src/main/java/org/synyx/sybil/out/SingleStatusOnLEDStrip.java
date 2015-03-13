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

        LOG.debug("Set color to: {}", color);

        outputLEDStrip.setFill(color);
        outputLEDStrip.updateDisplay();
    }


    public Status getStatus() {

        return status;
    }


    public void setStatus(StatusInformation statusInformation) {

        LOG.debug("Show status information: {}", statusInformation.getStatus());

        status = statusInformation.getStatus();
    }


    public void turnOff() {

        outputLEDStrip.setFill(Color.BLACK);
        outputLEDStrip.updateDisplay();
    }
}
