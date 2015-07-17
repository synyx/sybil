package org.synyx.sybil.bricklet.output.ledstrip.domain;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.jenkins.domain.StatusInformation;


/**
 * LEDStripDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStripDTO {

    private LEDStripDomain domain;

    private StatusInformation status;

    private Sprite1D sprite;

    public Sprite1D getSprite() {

        if (sprite == null) {
            throw new AttributeEmptyException("sprite undefined");
        }

        return sprite;
    }


    public void setSprite(Sprite1D sprite) {

        this.sprite = sprite;
    }


    public StatusInformation getStatus() {

        if (status == null) {
            throw new AttributeEmptyException("status undefined");
        }

        return status;
    }


    public void setStatus(StatusInformation status) {

        this.status = status;
    }


    public LEDStripDomain getDomain() {

        if (domain == null) {
            throw new AttributeEmptyException("domain undefined");
        }

        return domain;
    }


    public void setDomain(LEDStripDomain domain) {

        this.domain = domain;
    }
}
