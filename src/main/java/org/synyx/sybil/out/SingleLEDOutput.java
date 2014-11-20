package org.synyx.sybil.out;

import org.synyx.sybil.in.Status;
import org.synyx.sybil.in.StatusInformation;


/**
 * @author  Tobias Theuer
 */
public class SingleLEDOutput implements SingleStatusOutput {

    private Color color;
    private LEDOutput ledOutput;

    public SingleLEDOutput(String host, int port, String uid, int chipType, int frameDuration, int length) {

        ledOutput = new LEDOutput(host, port, uid, chipType, frameDuration, length);
    }

    @Override
    public void close() {

        ledOutput.close();
    }


    @Override
    public void showStatus(StatusInformation statusInformation) {

        if (statusInformation.getStatus() == Status.CRITICAL) {
            color = new Color(16, 0, 0);
        } else if (statusInformation.getStatus() == Status.WARNING) {
            color = new Color(16, 16, 0);
        } else {
            color = new Color(0, 0, 0);
        }

        ledOutput.setColor(color);
    }
}
