package org.synyx.sybil.bricklet.output.ledstrip.domain;

import org.synyx.sybil.bricklet.output.ledstrip.Sprite1D;
import org.synyx.sybil.jenkins.domain.StatusInformation;


/**
 * LEDStripDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class LEDStripDTO {

    private LEDStripConfig config;

    private StatusInformation status;

    private Sprite1D sprite;

    LEDStripDTO() {

        // default constructor deliberately left empty, needed for Mockito @spy
    }


    public LEDStripDTO(LEDStripConfig config) {

        this.config = config;
    }

    public Sprite1D getSprite() {

        return sprite;
    }


    public void setSprite(Sprite1D sprite) {

        this.sprite = sprite;
    }


    public StatusInformation getStatus() {

        return status;
    }


    public void setStatus(StatusInformation status) {

        this.status = status;
    }


    public LEDStripConfig getConfig() {

        return config;
    }
}
