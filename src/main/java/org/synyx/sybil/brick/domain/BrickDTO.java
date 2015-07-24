package org.synyx.sybil.brick.domain;

/**
 * BrickDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickDTO {

    private final BrickDomain domain;

    public BrickDTO(BrickDomain domain) {

        this.domain = domain;
    }

    public BrickDomain getDomain() {

        return domain;
    }
}
