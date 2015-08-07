package org.synyx.sybil.relay.service;

/**
 * BrickNotFoundException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class RelayNotFoundException extends RuntimeException {

    public RelayNotFoundException(String message) {

        super(message);
    }


    public RelayNotFoundException(String message, Throwable cause) {

        super(message, cause);
    }
}
