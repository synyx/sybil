package org.synyx.sybil.brick.domain;

/**
 * BrickDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class BrickDTO {

    private final BrickConfig config;

    public BrickDTO(BrickConfig config) {

        this.config = config;
    }

    public BrickConfig getConfig() {

        return config;
    }
}
