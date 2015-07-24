package org.synyx.sybil.bricklet.input.illuminance.domain;

/**
 * IlluminanceDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceDTO {

    private final IlluminanceDomain domain;

    public IlluminanceDTO(IlluminanceDomain domain) {

        this.domain = domain;
    }

    public IlluminanceDomain getDomain() {

        return domain;
    }
}
