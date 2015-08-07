package org.synyx.sybil.relay.service;

/**
 * RelayConnectionException.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public class RelayConnectionException extends RuntimeException {

    public RelayConnectionException(String message) {

        super(message);
    }


    public RelayConnectionException(String message, Throwable cause) {

        super(message, cause);
    }
}
