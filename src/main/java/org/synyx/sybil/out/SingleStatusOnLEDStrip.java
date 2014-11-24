package org.synyx.sybil.out;

import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;


/**
 * @author  Tobias Theuer
 */
public class SingleStatusOnLEDStrip implements SingleStatusOutput {

    private Color color;
    private OutputLEDStrip outputLEDStrip;

    public SingleStatusOnLEDStrip(OutputLEDStrip outputLEDStrip) {

        this.outputLEDStrip = outputLEDStrip;
    }

    @Override
    public void showStatus(StatusInformation statusInformation) {

        if (statusInformation.getStatus() == Status.CRITICAL) {
            color = new Color(127, 0, 0);
        } else if (statusInformation.getStatus() == Status.WARNING) {
            color = new Color(127, 127, 0);
        } else {
            color = new Color(0, 0, 0);
        }

        outputLEDStrip.setColor(color);
    }
}
