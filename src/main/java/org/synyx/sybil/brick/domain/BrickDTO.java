package org.synyx.sybil.brick.domain;

import org.synyx.sybil.AttributeEmptyException;


/**
 * BrickDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickDTO {

    private BrickDomain domain;

    public BrickDomain getDomain() {

        if (domain == null) {
            throw new AttributeEmptyException("Domain undefined");
        }

        return domain;
    }


    public void setDomain(BrickDomain domain) {

        this.domain = domain;
    }
}
