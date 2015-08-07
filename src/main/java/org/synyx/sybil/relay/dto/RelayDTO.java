package org.synyx.sybil.relay.dto;

/**
 * RelayDTO.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class RelayDTO {

    boolean state;

    public RelayDTO(boolean state) {

        this.state = state;
    }


    public RelayDTO() {

        // default constructor deliberately left empty
    }

    public boolean isPowered() {

        return state;
    }
}
