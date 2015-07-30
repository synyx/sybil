package org.synyx.sybil.bricklet.input.illuminance.domain;

/**
 * IlluminanceDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class IlluminanceDTO {

    private final IlluminanceConfig config;

    public IlluminanceDTO(IlluminanceConfig config) {

        this.config = config;
    }

    public IlluminanceConfig getConfig() {

        return config;
    }
}
