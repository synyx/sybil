package org.synyx.sybil.bricklet.output.ledstrip.domain;

import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.bricklet.output.ledstrip.database.LEDStripDomain;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.util.List;


/**
 * LEDStripDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStripDTO {

    public LEDStripDomain domain;

    public StatusInformation status;

    public List<Sprite1D> sprites;

    public double brightness;
}
