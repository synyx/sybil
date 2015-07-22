package org.synyx.sybil.bricklet.input.illuminance.domain;

import org.synyx.sybil.AttributeEmptyException;


/**
 * IlluminanceDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceDTO {

    IlluminanceDomain domain;

    public IlluminanceDomain getDomain() {

        if (domain == null) {
            throw new AttributeEmptyException("domain undefined");
        }

        return domain;
    }


    public void setDomain(IlluminanceDomain domain) {

        this.domain = domain;
    }
}
