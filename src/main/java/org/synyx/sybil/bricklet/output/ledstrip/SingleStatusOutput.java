package org.synyx.sybil.bricklet.output.ledstrip;

import org.synyx.sybil.jenkins.domain.StatusInformation;


/**
 * Shows a single status.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public interface SingleStatusOutput {

    /**
     * Shows the given status on an output device.
     *
     * @param  statusInformation  The StatusInformation to display.
     */
    void showStatus(StatusInformation statusInformation);
}
